package npo.kib.odc_demo.feature_app.domain.use_cases

import kotlinx.coroutines.*
import npo.kib.odc_demo.feature_app.data.db.Amount
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository

/** All methods are main-safe */
class GetInfoFromWalletUseCase(
    private val walletRepository: WalletRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     *  Get stored Name, wid, photo, etc
     * */
    suspend fun getLocalUserInfo(): UserInfo =  walletRepository.getLocalUserInfo()

    /** Current balance */
    suspend fun getSumInWallet(): Int? = walletRepository.getStoredInWalletSum()

    /** To get all the available banknotes info */
    suspend fun getStoredBanknotesIdsAmounts() : List<Amount> = walletRepository.getBanknotesIdsAndAmounts()


    private suspend fun <T> io(block: suspend CoroutineScope.() -> T): T =
        withContext(ioDispatcher) { block() }

}