package npo.kib.odc_demo.domain

import npo.kib.odc_demo.wallet.model.Amount
import npo.kib.odc_demo.wallet.model.data_packet.variants.UserInfo
import npo.kib.odc_demo.wallet_repository.repository.WalletRepository
import javax.inject.Inject

/** All methods are main-safe */
class GetInfoFromWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
) {
    /**
     *  Get stored Name, wid, photo, etc
     * */
    suspend fun getLocalUserInfo(): UserInfo = walletRepository.getLocalUserInfo()

    /** Current balance */
    suspend fun getSumInWallet(): Int = walletRepository.getStoredInWalletSum() ?: 0

    /** To get all the available banknotes info */
    suspend fun getStoredBanknotesIdsAmounts(): List<Amount> =
        walletRepository.getBanknotesIdsAndAmounts()

    /**Call [WalletRepository.registerWallet] to register wallet,
     * used at the app launch to keep the splashScreen active until is registered. */
    suspend fun registerWallet() = walletRepository.registerWallet()


    suspend fun isWalletRegistered() = walletRepository.isWalletRegistered()

}