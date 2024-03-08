package npo.kib.odc_demo.feature_app.presentation.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.repository.WalletRepository
import npo.kib.odc_demo.feature_app.domain.use_cases.GetInfoFromWalletUseCase
import javax.inject.Inject

//Top-level ViewModel to load and store things like current AppUser
//Probably will need to elevate when adding log-in onboarding
@HiltViewModel
class FeatureAppViewModel @Inject constructor(
    private val useCase: GetInfoFromWalletUseCase
) : ViewModel() {

    //todo: balance and AppUser states for the balance block

    private var currentJob : Job? = null

    private var _appState = MutableStateFlow<ODCAppState?>(null)
    val appState = _appState.asStateFlow()

    private var _currentBalance: MutableStateFlow<Int?> = MutableStateFlow(null)
    val currentBalance = _currentBalance.asStateFlow()

    var balanceUpdatingFinished: Boolean by mutableStateOf(false)
        private set

    init {
        currentJob = viewModelScope.launch(Dispatchers.IO) {
            balanceUpdatingFinished = false
            _currentBalance.update { useCase.getSumInWallet() }
            balanceUpdatingFinished = true
        }
    }

    fun getLocalUserInfo(){

    }


    fun logOut() {}

}