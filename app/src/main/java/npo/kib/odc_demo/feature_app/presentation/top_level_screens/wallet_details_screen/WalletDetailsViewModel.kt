package npo.kib.odc_demo.feature_app.presentation.top_level_screens.wallet_details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.UserInfo
import npo.kib.odc_demo.feature_app.domain.use_cases.GetInfoFromWalletUseCase
import javax.inject.Inject

@HiltViewModel
class WalletDetailsViewModel @Inject constructor(
    private val useCase: GetInfoFromWalletUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletDetailsState())
    val uiState = _uiState.asStateFlow()


    init {
        updateInfo()
    }

    fun updateInfo() {
        viewModelScope.launch {
            val amountsList = useCase.getStoredBanknotesIdsAmounts().map { it.amount }
            val nominalToCount = amountsList.groupingBy { it }.eachCount().toSortedMap(
                compareByDescending { it })
            val userInfo = useCase.getLocalUserInfo()
            _uiState.update {
                it.copy(
                    banknotesNominalToCountMap = nominalToCount,
                    userInfo = userInfo
                )
            }
        }
    }

}

data class WalletDetailsState(
    val banknotesNominalToCountMap: Map<Int, Int> = emptyMap(),
    val userInfo: UserInfo = UserInfo("", "")
)