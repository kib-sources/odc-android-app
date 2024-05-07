package npo.kib.odc_demo.core.domain

import npo.kib.odc_demo.core.wallet_repository.repository.WalletRepository
import javax.inject.Inject

//@ViewModelScoped
class AtmUseCase @Inject constructor(private val walletRepository: WalletRepository) {

    suspend fun sendAmountRequestToServer(amount : Int) = walletRepository.issueBanknotes(amount)

}