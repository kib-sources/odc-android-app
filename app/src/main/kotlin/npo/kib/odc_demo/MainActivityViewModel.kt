package npo.kib.odc_demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.MainActivityUiState.*
import npo.kib.odc_demo.core.common.data.util.log
import npo.kib.odc_demo.core.datastore.model.UserPreferences
import npo.kib.odc_demo.core.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.core.domain.GetInfoFromWalletUseCase
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val useCase: GetInfoFromWalletUseCase,
    private val defaultDatastore: DefaultDataStoreRepository
) : ViewModel() {

    //In init{} the wallet is getting registered, ui is in loading state,
    // may be showing status updates about wallet registration, then on success
    // show the ui, else do not crash the app and show a retry button

    private val _uiState: MutableStateFlow<MainActivityUiState> = MutableStateFlow(Loading)
    val uiState = _uiState.asStateFlow()

    private val userPreferences: StateFlow<UserPreferences> =
        defaultDatastore.userPreferencesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    init {
        registerWalletWithBank()
    }

    suspend fun isWalletRegistered() = useCase.isWalletRegistered()

    fun registerWalletWithBank() {
        viewModelScope.launch(Dispatchers.IO) {
            this.log("Registering wallet with bank")
            registerWallet()
            val registered = isWalletRegistered()
            _uiState.value = if (registered) Success(userPreferences.value)
            else FailureConnectingToBank
            this.log(if (registered) "Wallet registered" else "Failed registering wallet")
        }
    }

    private suspend fun registerWallet() = useCase.registerWallet()

    override fun onCleared() {
        this.log("onCleared()")
        super.onCleared()
    }

}

sealed interface MainActivityUiState {
    /** Keeping the splashscreen on. The wallet is being registered at this point
     * and other app resources are loading. */
    data object Loading : MainActivityUiState
    data object FailureConnectingToBank : MainActivityUiState
    data class Success(val userPreferences: UserPreferences) :
        MainActivityUiState
}
