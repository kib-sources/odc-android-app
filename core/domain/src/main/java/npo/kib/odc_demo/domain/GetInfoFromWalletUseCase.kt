package npo.kib.odc_demo.domain

import npo.kib.odc_demo.database.Amount
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.wallet.WalletRepository
import javax.inject.Inject

/** All methods are main-safe */
class GetInfoFromWalletUseCase @Inject constructor(
    private val walletRepository: npo.kib.odc_demo.wallet.WalletRepository,
) {
    /**
     *  Get stored Name, wid, photo, etc
     * */
    suspend fun getLocalUserInfo(): UserInfo = walletRepository.getLocalUserInfo()

    /** Current balance */
    suspend fun getSumInWallet(): Int = walletRepository.getStoredInWalletSum() ?: 0

    /** To get all the available banknotes info */
    suspend fun getStoredBanknotesIdsAmounts(): List<npo.kib.odc_demo.database.Amount> =
        walletRepository.getBanknotesIdsAndAmounts()

    /**Call [WalletRepository.getOrRegisterWallet] to register wallet,
     * used at the app launch to keep the splashScreen active until is registered. */
    suspend fun registerWallet() {
        walletRepository.getOrRegisterWallet()
    }

    suspend fun isWalletRegistered() = walletRepository.isWalletRegistered()

}