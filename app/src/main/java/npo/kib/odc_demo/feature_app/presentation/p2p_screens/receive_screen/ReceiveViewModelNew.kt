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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.di.ReceiveUseCase
import npo.kib.odc_demo.feature_app.di.ReceiverControllerBluetooth
import npo.kib.odc_demo.feature_app.domain.model.DataPacket
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.p2p.bluetooth.P2PConnectionBluetooth
import npo.kib.odc_demo.feature_app.domain.transaction_logic.ReceiverTransactionController
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PBaseUseCase

class ReceiveViewModelNew @AssistedInject constructor(
    @ReceiverControllerBluetooth _transactionController: ReceiverTransactionController,
    @Assisted private val registry: ActivityResultRegistry
) : ViewModel() {

    private val transactionController : ReceiverTransactionController = _transactionController
    private val p2pBluetoothConnection = transactionController.p2pConnection as P2PConnectionBluetooth


    //todo combine different flows here in one flow of receiveScreenState

    private val _uiState: MutableStateFlow<ReceiveUiState> =
        MutableStateFlow(ReceiveUiState.Initial)
//    val uiState: StateFlow<ReceiveUiState>
//        get() = _uiState.asStateFlow()

//    private val receivedPacketsChannel = Channel<DataPacket?>(capacity = UNLIMITED)
//    private val receivedPacketsFlow: Flow<DataPacket?> = receivedPacketsChannel.receiveAsFlow()


    //    val state : MutableStateFlow<ReceiveScreenState> = MutableStateFlow(
//        ReceiveScreenState()
//    )
    val state: StateFlow<ReceiveScreenState> = MutableStateFlow(
        ReceiveScreenState()
    )


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

            is ReceiveScreenEvent.ReactToConnection -> {
                when (event.accept) {
                    false -> {}
                    true -> {}
                }
            }

            ReceiveScreenEvent.Reset -> {
                stopAdvertising()
//                p2pUseCase.stopDiscovery()
            }

            ReceiveScreenEvent.Finish -> {}
        }

    }


    private fun startAdvertising() {
        viewModelScope.launch {
            //Duration of 0 corresponds to indefinite advertising. Unrecommended. Stop advertising manually after.
            //Edit: passing 0 actually makes system prompt for default duration (120 seconds)
            p2pBluetoothConnection.startAdvertising(
                registry = registry,
                duration = 10,
                callback = { resultDuration ->
                    resultDuration?.run {
                        _uiState.update { ReceiveUiState.Advertising }
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

    private fun acceptConnection() {
        //update ui state
        //...
        TODO()
    }

    private fun rejectConnection() {
        //update ui state
        //...
        TODO()
    }

    private fun acceptOffer() {
        //update ui state
        //...
        TODO()
    }

    private fun rejectOffer() {
        //update ui state
        //...
        TODO()
    }


    private fun reset() {
        transactionController.reset()
    }


    private fun Flow<BluetoothConnectionStatus>.listen(): Job {
        return onEach { result ->
            when (result) {
                is BluetoothConnectionStatus.ConnectionEstablished -> {
                    _uiState.update {
                        ReceiveUiState.Connected
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