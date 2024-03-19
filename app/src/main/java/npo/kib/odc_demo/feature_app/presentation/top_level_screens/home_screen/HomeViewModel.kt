package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.user.toAppUser
import npo.kib.odc_demo.feature_app.domain.use_cases.GetInfoFromWalletUseCase
import npo.kib.odc_demo.feature_app.domain.util.log
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: GetInfoFromWalletUseCase
) : ViewModel() {

    private val _homeScreenState: MutableStateFlow<HomeScreenState> =
        MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    init {
        updateBalanceAndAppUserInfo()
    }

    //Use on the "refresh" button of the balance block
    fun updateBalanceAndAppUserInfo() {
        if (!homeScreenState.value.isUpdatingBalanceAndInfo) {
            viewModelScope.launch {
                this@HomeViewModel.log("Updating balance")
                _homeScreenState.update { it.copy(isUpdatingBalanceAndInfo = true) }
                val balance = async { useCase.getSumInWallet() }
                val currentAppUser = async { useCase.getLocalUserInfo().toAppUser() }
                _homeScreenState.update {
                    it.copy(
                        balance = balance.await(), currentUser = currentAppUser.await()
                    )
                }
                this@HomeViewModel.log("Balance updated")
            }
            _homeScreenState.update { it.copy(isUpdatingBalanceAndInfo = false) }
        }
    }

    override fun onCleared() {
        this.log("onCleared()")
        super.onCleared()
    }
}