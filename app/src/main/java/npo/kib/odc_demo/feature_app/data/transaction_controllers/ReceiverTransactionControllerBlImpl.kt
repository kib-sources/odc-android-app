package npo.kib.odc_demo.feature_app.data.transaction_controllers

import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnection
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController


/**
 * Bluetooth implementation of [ReceiverTransactionController], uses [P2PConnectionBluetooth]
 * */
class ReceiverTransactionControllerBlImpl(override val p2pConnection: P2PConnection, override val walletRepository: WalletRepository) :
    ReceiverTransactionController() {
    override fun listenForOffer() {
        TODO("Not yet implemented")
    }

    override fun sendOfferRejection() {
        TODO("Not yet implemented")
    }

    override fun sendOfferApproval() {
        TODO("Not yet implemented")
    }

    override fun listenForBanknotes() {
        TODO("Not yet implemented")
    }

    override fun initBanknoteVerification() {
        TODO("Not yet implemented")
    }

    override fun sendAcceptanceBlocks() {
        TODO("Not yet implemented")
    }

    override fun verifyReceivedBlockSignature() {
        TODO("Not yet implemented")
    }

    override fun saveBanknotesToWallet() {
        TODO("Not yet implemented")
    }

    override fun sendResult() {
        TODO("Not yet implemented")
    }


}