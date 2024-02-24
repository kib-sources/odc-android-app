package npo.kib.odc_demo.feature_app.domain.transaction_logic

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.*
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.ForSender.*
import npo.kib.odc_demo.feature_app.domain.transaction_logic.TransactionSteps.TransactionRole.SENDER
import npo.kib.odc_demo.feature_app.domain.transaction_logic.util.findBanknotesWithSum


class SenderTransactionController(
    walletRepository: WalletRepository,
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TransactionController(
    scope = scope, walletRepository = walletRepository, role = SENDER
) {

    private val _currentStep: MutableStateFlow<ForSender> = MutableStateFlow(INITIAL)
    override val currentStep: StateFlow<ForSender> = _currentStep.asStateFlow()

    init {
        initController()
    }

    public override fun initController(): Boolean {
        val result = super.initController()
        if (result) updateStep(INITIAL)
        return result
    }

    fun startProcessingIncomingPackets() {
        receivedPacketsFlow.onEach { packet ->
            processPacketOnCurrentStep(packet)
        }.onCompletion { withContext(NonCancellable) { resetController() } }.launchIn(scope)
    }

    private suspend fun processPacketOnCurrentStep(packet: DataPacketVariant) {
        if (packet.packetType == USER_INFO) {
            updateOtherUserInfo(packet as UserInfo)
        } else when (currentStep.value) {
            INITIAL -> {
                //nothing is expected to be received here yet
                //initial step
            }
            WAITING_FOR_OFFER_RESPONSE -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                val result = (packet as TransactionResult).value
                when (result) {
                    TransactionResult.ResultType.Success -> {
                        //when accepted, send BanknotesList
                        sendBanknotesList()
                    }
                    is TransactionResult.ResultType.Failure -> {

                        updateStep(INITIAL)
                    }
                }
            }
            WAITING_FOR_BANKNOTES_LIST_RECEIVED_RESPONSE -> {
                packet.requireToBeOfTypes(TRANSACTION_RESULT)
                val result = (packet as TransactionResult).value
                when (result) {
                    TransactionResult.ResultType.Success -> {

                    }
                    is TransactionResult.ResultType.Failure -> {
                        //If an invalid BanknotesList was sent (empty, etc)
                        updateStep(INITIAL)
                    }
                }
            }
            WAITING_FOR_ACCEPTANCE_BLOCKS -> {
                packet.requireToBeOfTypes(ACCEPTANCE_BLOCKS)
                onAcceptanceBlocksReceived(packet as AcceptanceBlocks)
            }
            SIGN_AND_SEND_BLOCK -> {}
            FINISH -> {}
        }
    }

    suspend fun tryConstructAmount(amount: Int): Boolean {
        _transactionDataBuffer.update { it.copy(isAmountAvailable = null) }
        val resultBanknotes = getBanknotesFromAmount(amount)
        return if (resultBanknotes == null) {
            _transactionDataBuffer.update { it.copy(isAmountAvailable = false) }
            false
        } else {
            _transactionDataBuffer.update {
                it.copy(
                    isAmountAvailable = true, banknotesList = BanknotesList(resultBanknotes),
                    amountRequest = AmountRequest(
                        amount = amount, walletId = walletRepository.getOrRegisterWallet().walletId
                    )
                )
            }
            true
        }
    }

    private suspend fun sendOffer() {
        if (transactionDataBuffer.value.isAmountAvailable == true) {
            updateStep(WAITING_FOR_OFFER_RESPONSE)
            outputDataPacketChannel.send(transactionDataBuffer.value.amountRequest!!)
        } else throw TransactionException(
            "Cannot send the offer, the amount is not available in transactionDataBuffer"
        )
    }

    private suspend fun sendBanknotesList() {
        val list = transactionDataBuffer.value.banknotesList!!.also {
            if (it.list.isEmpty()) throw TransactionException(
                "Tried sending banknotes list but it is empty in transactionDataBuffer"
            )
        }
        outputDataPacketChannel.send(list)
    }

    private suspend fun onAcceptanceBlocksReceived(acceptanceBlocks: AcceptanceBlocks) {
        updateLastAcceptanceBLocks(acceptanceBlocks)
        withContext(defaultDispatcher) {
            getSignedBlock(acceptanceBlocks.childBlock, acceptanceBlocks.protectedBlock)
        }
    }

    private suspend fun getSignedBlock(unsignedNewBlock: Block, protectedBlock: Block) {
        withContext(defaultDispatcher) {
            val signedBlock: Block =

                updateLastSignedBLock(signedBlock)
        }
    }

    private suspend fun deleteLocalBanknotes() {
        withContext(NonCancellable) {
            val bnidList =
                transactionDataBuffer.value.banknotesList?.list?.map { it.banknoteWithProtectedBlock.banknote.bnid }
            if (bnidList == null) throw TransactionException(
                "BanknotesList in buffer is null"
            ) else if (bnidList.isEmpty()) throw TransactionException(
                "BanknotesList in buffer is empty"
            ) else walletRepository.deleteBanknotesWithBlockchainByBnids(bnidList)
        }
    }

    private fun updateStep(step: ForSender) {
        _currentStep.value = step
    }

    /**
     *  Subset Sum Problem, NP-Hard
     *  @see <a href="https://en.wikipedia.org/wiki/Subset_sum_problem">Subset Sum Problem - Wikipedia</a>
     * */
    private suspend fun getBanknotesFromAmount(amount: Int): List<BanknoteWithBlockchain>? {
        val allAmounts = withContext(ioDispatcher) { walletRepository.getBanknotesIdsAndAmounts() }
        return withContext(defaultDispatcher) {
            val resultAmounts = findBanknotesWithSum(
                banknotesIdsAmounts = allAmounts, targetSum = amount
            ) ?: return@withContext null
            val resultBnids = resultAmounts.map { it.bnid }
            return@withContext walletRepository.getBanknotesWithBlockchainByBnids(resultBnids)
        }
    }
}