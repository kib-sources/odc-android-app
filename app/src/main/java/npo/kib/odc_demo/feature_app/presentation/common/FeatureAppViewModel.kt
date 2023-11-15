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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser
import npo.kib.odc_demo.feature_app.domain.repository.BankRepository
import npo.kib.odc_demo.feature_app.presentation.common.ui.ODCAppState

//Top-level ViewModel to load and store things like current AppUser
//Probably will need to elevate when adding log-in onboarding

class FeatureAppViewModel @AssistedInject constructor(
    @Assisted appUser: AppUser, private val bankRepository: BankRepository
) : ViewModel() {

    //todo: balance and AppUser states for the balance block

    private var _appState = MutableStateFlow<ODCAppState?>(null)
    val appState = _appState.asStateFlow()

    private var _currentBalance: MutableStateFlow<Int?> = MutableStateFlow(null)
    val currentBalance = _currentBalance.asStateFlow()

    var balanceUpdatingFinished : Boolean by mutableStateOf(false)
        private set

    init {
        val job = viewModelScope.launch(Dispatchers.IO) {
            balanceUpdatingFinished = false
            bankRepository.getSumAsFlow().collect {
                _currentBalance.emit(it) }
        }
        job.invokeOnCompletion { balanceUpdatingFinished = true }
    }

    fun logOut() {}


    @AssistedFactory
    interface Factory {
        fun create(appuser : AppUser): FeatureAppViewModel

    }

    companion object{

            fun provideFeatureAppViewModelFactory(
                factory: FeatureAppViewModel.Factory, appUser: AppUser
            ): ViewModelProvider.Factory {
                return object : ViewModelProvider.Factory {

                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return factory.create(appUser) as T
                    }

                }
            }

    }

}
