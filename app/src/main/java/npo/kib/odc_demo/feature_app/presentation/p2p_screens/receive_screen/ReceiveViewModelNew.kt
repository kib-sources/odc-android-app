package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.di.ReceiveUseCase
import npo.kib.odc_demo.feature_app.domain.model.DataPacket
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants.AmountRequest
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.CustomBluetoothDevice
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCase
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.nearby_screen.BaseP2PViewModel

class ReceiveViewModelNew @AssistedInject constructor(
    @ReceiveUseCase useCase: P2PBaseUseCase, @Assisted private val registry: ActivityResultRegistry
) : BaseP2PViewModel() {

    override val p2pUseCase = useCase as P2PReceiveUseCase

    private val p2pBluetoothConnection =
        p2pUseCase.p2pConnection as P2PConnectionBluetooth



    private val _uiState: MutableStateFlow<ReceiveUiState> =
        MutableStateFlow(ReceiveUiState.Initial)
    val uiState: StateFlow<ReceiveUiState>
        get() = _uiState.asStateFlow()

    //todo add other device info class (potentially with serialized profile pic field)
    // on each emission from bluetoothPacketsFlow retrieve first fields and send in flow of type
    // of that device info class and transform to flow of bytes without those first
    //  bluetooth packet user-specific info fields

//    private val _receivedBluetoothPacketsChannel = Channel<BluetoothDataPacket?>()
//    private val receivedBluetoothPacketsFlow: Flow<BluetoothDataPacket?> = _receivedBluetoothPacketsChannel.receiveAsFlow()
    private val receivedBluetoothPacketsChannel = Channel<DataPacket?>(capacity = UNLIMITED)
    private val receivedBluetoothPacketsFlow: Flow<DataPacket?> = receivedBluetoothPacketsChannel.receiveAsFlow()


//    private val receivedBytesFlow : Mu
//    private val _viewModelState: MutableStateFlow<ReceiveUiState> =
//        MutableStateFlow(ReceiveUiState.Initial)
//    val viewModelState: StateFlow<ReceiveUiState>
//        get() = _viewModelState.asStateFlow()


    private var deviceConnectionJob: Job? = null


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
//                p2pUseCase.stopDiscovery()
            }
        }

    }


    private fun startAdvertising() {
        viewModelScope.launch {
            //Duration of 0 corresponds to indefinite advertising. Unrecommended. Stop advertising manually after.
            //Edit: passing 0 actually makes system prompt for default duration (120 seconds)
            p2pBluetoothConnection.startAdvertising(registry = registry,
                duration = 10,
                callback = { resultDuration ->
                    resultDuration?.run {
                        _uiState.update { ReceiveUiState.WaitingForConnection }
                        deviceConnectionJob?.cancel()
                        deviceConnectionJob =
                            p2pBluetoothConnection.startBluetoothServerAndGetFlow().listen()
                    }
                })
        }

    }

    //Due to a bug (?) in Android some devices will start advertising for 120s instead of 1s
    private fun stopAdvertising() {
        viewModelScope.launch {
            p2pBluetoothConnection.stopAdvertising(registry)
//            _uiState.update { ReceiveUiState.Initial }
        }
    }


    private fun Flow<BluetoothConnectionStatus>.listen(): Job {
        return onEach { result ->
            when (result) {
                is BluetoothConnectionStatus.ConnectionEstablished -> {
                    _uiState.update {ReceiveUiState.Connected(otherDevice = result.withDevice)
                    }
                }
                is BluetoothConnectionStatus.TransferSucceeded -> {
//                    receivedBluetoothPacketsChannel.send(result.bytes)
                }
                is BluetoothConnectionStatus.Error -> {}
                BluetoothConnectionStatus.WaitingForConnection -> {}
                BluetoothConnectionStatus.Disconnected -> {}
                BluetoothConnectionStatus.NoConnection -> {}
                BluetoothConnectionStatus.ConnectionInitiated -> {}
                BluetoothConnectionStatus.Discovering -> {}
            }
        }.catch { throwable ->
            p2pBluetoothConnection.closeConnection()
            _uiState.update {
                ReceiveUiState.Result(ReceiveUiState.ResultType.Failure("Exception: ${throwable.message}"))
            }
        }.launchIn(viewModelScope)
    }


    override fun onCleared() {
        super.onCleared()
        p2pBluetoothConnection.reset()
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
    data object WaitingForConnection : ReceiveUiState

    data class Connected(val otherDevice: CustomBluetoothDevice?) : ReceiveUiState
    data class OfferReceived(val amountRequest: AmountRequest) : ReceiveUiState
    data object Receiving : ReceiveUiState
    data class Result(val result: ResultType) : ReceiveUiState

    sealed interface ResultType {
        data object Success : ResultType
        data class Failure(val failureMessage: String) : ResultType
    }
}
