package npo.kib.odc_demo.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.common.data.util.log
import npo.kib.odc_demo.datastore.UtilityDataStoreObject.SHOULD_UPDATE_UI_USER_INFO
import npo.kib.odc_demo.datastore.UtilityDataStoreRepository
import npo.kib.odc_demo.domain.GetInfoFromWalletUseCase
import npo.kib.odc_demo.wallet.model_extensions.asAppUser
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val utilDataStore: UtilityDataStoreRepository,
    private val useCase: GetInfoFromWalletUseCase
) : ViewModel() {

    private val _homeScreenState: MutableStateFlow<HomeScreenState> =
        MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()


    init {
        this.log("Init")
        viewModelScope.launch { utilDataStore.writeValue(SHOULD_UPDATE_UI_USER_INFO, true) }
        utilDataStore.publicUtilDataFlow.mapLatest {
            this@HomeViewModel.log("mapLatest{}, SHOULD_UPDATE_UI_USER_INFO: ${it.shouldUpdateUiUserInfo} ")
            if (it.shouldUpdateUiUserInfo) {
                updateBalanceAndAppUserInfo()
                utilDataStore.writeValue(SHOULD_UPDATE_UI_USER_INFO, false)
                this@HomeViewModel.log("SHOULD_UPDATE_UI_USER_INFO set to false")
            }
        }.launchIn(viewModelScope)
    }

    fun updateBalanceAndAppUserInfo() {
        if (!homeScreenState.value.isUpdatingBalanceAndInfo) {
            viewModelScope.launch {
                _homeScreenState.update { it.copy(isUpdatingBalanceAndInfo = true) }
                this@HomeViewModel.log("Updating balance")
                val balance = async { useCase.getSumInWallet() }
                val currentAppUser = async { useCase.getLocalUserInfo().asAppUser() }
                _homeScreenState.update {
                    it.copy(balance = balance.await(), currentUser = currentAppUser.await())
                }
                this@HomeViewModel.log("Balance updated")
                _homeScreenState.update { it.copy(isUpdatingBalanceAndInfo = false) }
            }
        }
    }

    override fun onCleared() {
        this.log("onCleared()")
        super.onCleared()
    }
}