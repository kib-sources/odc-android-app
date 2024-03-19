package npo.kib.odc_demo.feature_app.domain.use_cases

import dagger.hilt.android.scopes.ViewModelScoped
import npo.kib.odc_demo.feature_app.data.db.Amount
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
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

    /**Call [WalletRepository.getOrRegisterWallet] to register wallet,
     * used at the app launch to keep the splashScreen active until is registered. */
    suspend fun registerWallet() {
        walletRepository.getOrRegisterWallet()
    }

    suspend fun isWalletRegistered() = walletRepository.isWalletRegistered()

}