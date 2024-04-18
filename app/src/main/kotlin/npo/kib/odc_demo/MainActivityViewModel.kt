package npo.kib.odc_demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.model.user.UserPreferences
import npo.kib.odc_demo.datastore.DefaultDataStoreRepository
import npo.kib.odc_demo.domain.GetInfoFromWalletUseCase
import npo.kib.odc_demo.domain.util.log
import npo.kib.odc_demo.feature_app.presentation.common.MainActivityUiState.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val useCase: npo.kib.odc_demo.domain.GetInfoFromWalletUseCase,
    private val defaultDatastore: npo.kib.odc_demo.datastore.DefaultDataStoreRepository
) : ViewModel() {

    //In init{} the wallet is getting registered, ui is in loading state,
    // may be showing status updates about wallet registration, then on success
    // show the ui, else do not crash the app and show a retry button

    private val _uiState: MutableStateFlow<MainActivityUiState> = MutableStateFlow(Loading)
    val uiState = _uiState.asStateFlow()

    private val userPreferences: StateFlow<npo.kib.odc_demo.model.user.UserPreferences> =
        defaultDatastore.userPreferencesFlow.stateIn(
            scope = viewModelScope,
            initialValue = npo.kib.odc_demo.model.user.UserPreferences(),
            started = SharingStarted.WhileSubscribed(5_000),
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
    data class Success(val userPreferences: npo.kib.odc_demo.model.user.UserPreferences) :
        MainActivityUiState
}
