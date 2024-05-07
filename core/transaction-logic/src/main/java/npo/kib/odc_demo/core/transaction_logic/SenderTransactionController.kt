package npo.kib.odc_demo.core.transaction_logic

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import npo.kib.odc_demo.core.common.data.util.log
import npo.kib.odc_demo.core.transaction_logic.algorithms.findBanknotesWithSum
import npo.kib.odc_demo.core.transaction_logic.model.TransactionRole.SENDER
import npo.kib.odc_demo.core.transaction_logic.model.TransactionStatus.SenderTransactionStatus
import npo.kib.odc_demo.core.transaction_logic.model.TransactionStatus.SenderTransactionStatus.*
import npo.kib.odc_demo.core.transaction_logic.model.TransactionSteps.ForSender
import npo.kib.odc_demo.core.transaction_logic.model.TransactionSteps.ForSender.*
import npo.kib.odc_demo.core.transaction_logic.model.TransactionSteps.ForSender.WAITING_FOR_ACCEPTANCE_BLOCKS
import npo.kib.odc_demo.core.transaction_logic.model.TransactionSteps.ForSender.WAITING_FOR_OFFER_RESPONSE
import npo.kib.odc_demo.core.wallet.model.BanknoteWithBlockchain
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacketType.*
import npo.kib.odc_demo.core.wallet.model.data_packet.variants.*
import npo.kib.odc_demo.core.wallet_repository.repository.WalletRepository
import javax.inject.Inject


@ViewModelScoped
class SenderTransactionController @Inject constructor(
    walletRepository: WalletRepository
) : TransactionController(
    walletRepository = walletRepository, role = SENDER
) {
    private val _currentStep: MutableStateFlow<ForSender> = MutableStateFlow(INITIAL_STEP)
    private val currentStep: StateFlow<ForSender> = _currentStep.asStateFlow()

    private fun updateStep(step: ForSender) {
        _currentStep.value = step
    }

    private val _transactionStatus: MutableStateFlow<SenderTransactionStatus> = MutableStateFlow(
        INITIAL
    )
    val transactionStatus = _transactionStatus.asStateFlow()

    private fun updateStatus(newStatus: SenderTransactionStatus) {
        _transactionStatus.value = newStatus
    }

    override fun onTransactionError() {
        updateStatus(ERROR)
        updateStep(FINISHED)
    }

    init {
        initController()
    }

    public override fun initController(): Boolean {
        val started = super.initController()
        if (started) {
            updateStep(INITIAL_STEP)
            updateStatus(INITIAL)
        }
        return started
    }

    override suspend fun processPacketOnCurrentStep(packet: DataPacketVariant) {
        // user info can be processed at any moment now
        if (packet.packetType == USER_INFO) {
            updateOtherUserInfo(packet as UserInfo)
        } else when (currentStep.value) {
            INITIAL_STEP -> {
                //nothing is expected to be received here yet
                //initial step where we can try to construct amount and then send it
                //when we send it (trigger manually through UI), the step is set to WAITING_FOR_OFFER_RESPONSE
            }

            WAITING_FOR_OFFER_RESPONSE -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                when ((packet as TransactionResult).value) {
                    TransactionResult.ResultType.Success -> {
                        //when accepted, send BanknotesList
                        updateStatus(OFFER_ACCEPTED)
                        sendBanknotesList()
                    }

                    is TransactionResult.ResultType.Failure -> {
                        //when rejected go back to initial and be able to send offer again or
                        // try to construct another amount
                        updateStep(INITIAL_STEP)
                        updateStatus(OFFER_REJECTED)
                    }
                }
            }

            WAITING_FOR_BANKNOTES_LIST_RECEIVED_RESPONSE -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                val result = packet as TransactionResult
                updateReceivedTransactionResult(result)
                when (result.value) {
                    TransactionResult.ResultType.Success -> {
                        updateStep(WAITING_FOR_ACCEPTANCE_BLOCKS)
                        updateStatus(SenderTransactionStatus.WAITING_FOR_ACCEPTANCE_BLOCKS)
                    }

                    is TransactionResult.ResultType.Failure -> {
                        //If an invalid BanknotesList was received
                        throw transactionExceptionWithRole("The receiver got an invalid BanknotesList")
                    }
                }
            }

            WAITING_FOR_ACCEPTANCE_BLOCKS -> {
                packet.requireToBeOfTypes(ACCEPTANCE_BLOCKS)
                onAcceptanceBlocksReceived(packet as AcceptanceBlocks)
            }

            WAITING_FOR_RESULT -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                val result = (packet as TransactionResult).value
                if (transactionDataBuffer.value.allBanknotesProcessed) {
                    if (result is TransactionResult.ResultType.Success) {
                        deleteLocalBanknotes()
                        withContext(NonCancellable) {
                            updateStatus(BANKNOTES_DELETED)
                            sendPositiveResult()
                            updateStep(FINISHED)
                            updateStatus(FINISHED_SUCCESSFULLY)
                        }
                    } else throw transactionExceptionWithRole("on WAITING_FOR_RESULT step, received result but no conditions were satisfied")
                }
            }

            FINISHED -> {
//                nothing is expected after the transaction has finished
            }
        }
    }

    /**
     *  Trying to construct amount, update transaction buffer on result,
     *  or throw [TransactionException][TransactionController.TransactionException] if called on the wrong step
     * */
    suspend fun tryConstructAmount(amount: Int): Boolean {
        return withContext(defaultDispatcher) {
            if (currentStep.value == INITIAL_STEP) {
                _transactionDataBuffer.update { it.copy(isAmountAvailable = null) }
                updateStatus(CONSTRUCTING_AMOUNT)
                this@SenderTransactionController.log("CONSTRUCTING_AMOUNT")
                //partially clear protected blocks of banknotes before sending to reduce size
                val resultBanknotes: List<BanknoteWithBlockchain>? = getBanknotesFromAmount(amount)?.map {
                    ensureActive()
                    val newProtectedBlock = walletRepository.walletInitProtectedBlock(
                        it.banknoteWithProtectedBlock.protectedBlock
                    )
                    it.copy(
                        banknoteWithProtectedBlock = it.banknoteWithProtectedBlock.copy(
                            protectedBlock = newProtectedBlock
                        )
                    )
                }
                return@withContext if (resultBanknotes == null) {
                    _transactionDataBuffer.update { it.copy(isAmountAvailable = false) }
                    updateStatus(SHOWING_AMOUNT_AVAILABILITY)
                    this@SenderTransactionController.log("SHOWING_AMOUNT_AVAILABILITY, isAmountAvailable = false")
                    false
                } else {
                    _transactionDataBuffer.update {
                        it.copy(
                            isAmountAvailable = true,
                            banknotesList = BanknotesList(resultBanknotes),
                            amountRequest = AmountRequest(
                                amount = amount,
                                walletId = walletRepository.getWalletId()!!
                            )
                        )
                    }
                    updateStatus(SHOWING_AMOUNT_AVAILABILITY)
                    this@SenderTransactionController.log("SHOWING_AMOUNT_AVAILABILITY, isAmountAvailable = true")
                    true
                }
            } else throw transactionExceptionWithRole("Tried constructing amount while not on INITIAL_STEP step")
        }
    }

    suspend fun trySendOffer() = when {
        currentStep.value != INITIAL_STEP -> throw transactionExceptionWithRole("Tried sending offer while not on INITIAL_STEP step")
        transactionDataBuffer.value.isAmountAvailable == true -> {
            updateStep(WAITING_FOR_OFFER_RESPONSE)
            updateStatus(SenderTransactionStatus.WAITING_FOR_OFFER_RESPONSE)
            outputDataPacketChannel.send(transactionDataBuffer.value.amountRequest!!)
        }

        else -> throw transactionExceptionWithRole("Cannot send the offer, the amount is not available in transactionDataBuffer")
    }


    private suspend fun sendBanknotesList() {
        updateStatus(SENDING_BANKNOTES_LIST)
        val banknotesList = transactionDataBuffer.value.banknotesList
            ?: throw transactionExceptionWithRole("Tried sending BanknotesList but it was null in the buffer.")
        if (banknotesList.list.isEmpty()) throw transactionExceptionWithRole("Tried sending banknotes list but it was empty in the buffer")
        outputDataPacketChannel.send(banknotesList)
        updateStatus(WAITING_FOR_BANKNOTES_RECEIVED_RESPONSE)
        updateStep(WAITING_FOR_BANKNOTES_LIST_RECEIVED_RESPONSE)
    }

    private suspend fun onAcceptanceBlocksReceived(acceptanceBlocks: AcceptanceBlocks) {
        withContext(defaultDispatcher) {
            updateLastAcceptanceBLocks(acceptanceBlocks)
            updateStatus(SIGNING_SENDING_NEW_BLOCK)
            val currentBanknoteLastBlock = currentProcessedBanknote.blocks.last()
            val resultBlock = walletRepository.walletSignature(
                parentBlock = currentBanknoteLastBlock,
                childBlock = acceptanceBlocks.childBlock,
                protectedBlock = acceptanceBlocks.protectedBlock
            )
            outputDataPacketChannel.send(resultBlock)
            val li = banknotesList.lastIndex
            val ci = currentBanknoteIndex
            when {
                ci < li -> {
                    currentBanknoteIndex += 1
                }

                ci == li -> {
                    _transactionDataBuffer.update { it.copy(allBanknotesProcessed = true) }
                    updateStatus(ALL_BANKNOTES_PROCESSED)
                    updateStep(WAITING_FOR_RESULT)
                }

                else -> throw transactionExceptionWithRole("currentBanknoteIndex > banknotesList.lastIndex")
            }
        }
    }

    private suspend fun deleteLocalBanknotes() {
        withContext(NonCancellable) {
            updateStatus(DELETING_BANKNOTES_FROM_WALLET)
            val bnidList = transactionDataBuffer.value.banknotesList?.list?.map { it.banknoteWithProtectedBlock.banknote.bnid }
            if (bnidList == null) throw transactionExceptionWithRole(
                "BanknotesList in buffer is null"
            ) else if (bnidList.isEmpty()) throw transactionExceptionWithRole(
                "BanknotesList in buffer is empty"
            ) else walletRepository.deleteBanknotesWithBlockchainByBnids(bnidList)
        }
    }

    /**
     *  Subset Sum Problem, NP-Hard
     *  @see <a href="https://en.wikipedia.org/wiki/Subset_sum_problem">Subset Sum Problem - Wikipedia</a>
     * */
    private suspend fun getBanknotesFromAmount(amount: Int): List<BanknoteWithBlockchain>? {
        val allAmounts = walletRepository.getBanknotesIdsAndAmounts()
        return withContext(defaultDispatcher) {
            val resultAmounts = findBanknotesWithSum(
                banknotesIdsAmounts = allAmounts, targetSum = amount
            ) ?: return@withContext null
            val resultBnids = resultAmounts.map { ensureActive(); it.bnid }
            return@withContext walletRepository.getBanknotesWithBlockchainByBnids(resultBnids)
        }
    }
}