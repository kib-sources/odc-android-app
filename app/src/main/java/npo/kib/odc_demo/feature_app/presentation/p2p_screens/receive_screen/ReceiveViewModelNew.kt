package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.di.ReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.p2p.P2PConnectionBidirectionalBluetooth
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseP2PViewModel
import javax.inject.Inject

class ReceiveViewModelNew @AssistedInject constructor(
    @ReceiveUseCase p2pUseCase: P2PBaseUseCase,
    @Assisted private val registry: ActivityResultRegistry
) : BaseP2PViewModel() {

    override val p2pUseCase = p2pUseCase as P2PReceiveUseCase

    private val p2pBluetoothConnection =
        p2pUseCase.p2pConnection as P2PConnectionBidirectionalBluetooth

    private val _uiState: MutableStateFlow<ReceiveUiState> =
        MutableStateFlow(ReceiveUiState.Initial)
    val uiState: StateFlow<ReceiveUiState>
        get() = _uiState.asStateFlow()

    init {

    }

    fun onEvent(event: ReceiveScreenEvent) {
        when (event) {
            is ReceiveScreenEvent.SetAdvertising -> {
                if (event.active) startAdvertising()
                else stopAdvertising()
            }

            is ReceiveScreenEvent.ReactToOffer -> {
                if (event.accept) {
                } else {
                }
            }

            ReceiveScreenEvent.Reset -> {
                stopAdvertising()
            }
        }

    }


    private fun startAdvertising() {
        viewModelScope.launch {
            //Duration of 0 corresponds to indefinite advertising. Unrecommended. Stop advertising manually after.
            val duration = 10
            p2pBluetoothConnection.startAdvertising(registry = registry, duration = duration)
            _uiState.update { ReceiveUiState.Advertising }
            delay(timeMillis = duration.toLong()*1000)
            _uiState.update { ReceiveUiState.Initial }
        }

    }

    private fun stopAdvertising() {
        viewModelScope.launch {
            p2pBluetoothConnection.stopAdvertising(registry)
            _uiState.update { ReceiveUiState.Initial }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(registry: ActivityResultRegistry): ReceiveViewModelNew
    }

    companion object {

        fun provideReceiveViewModelNewFactory(
            factory: Factory, registry: ActivityResultRegistry
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(registry) as T
                }

            }
        }
    }
}

// Add separate connection buffer class with builder to keep the current connection data
//like current sending user info, current chosen user, the selected banknotes amount, etc, to build
// along with the connection progression?
sealed interface ReceiveUiState {
    data object Initial : ReceiveUiState
    data object Advertising : ReceiveUiState

    data class Paired(val state: Boolean) : ReceiveUiState
    data object OfferReceived : ReceiveUiState
    data object Receiving : ReceiveUiState
    data class Result(val result: ResultType) : ReceiveUiState

    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}
