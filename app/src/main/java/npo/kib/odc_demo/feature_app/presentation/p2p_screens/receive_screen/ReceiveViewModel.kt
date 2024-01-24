package npo.kib.odc_demo.feature_app.presentation.p2p_screens.receive_screen

import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion
import npo.kib.odc_demo.feature_app.domain.model.connection_status.BluetoothConnectionStatus
import npo.kib.odc_demo.feature_app.domain.use_cases.P2PReceiveUseCaseNew

class ReceiveViewModel @AssistedInject constructor(
    private val useCase: P2PReceiveUseCaseNew,
    @Assisted private val registry: ActivityResultRegistry
) : ViewModel() {


    //todo combine different flows here in one flow of receiveScreenState

    private val _uiState: MutableStateFlow<ReceiveUiState> = MutableStateFlow(ReceiveUiState.Initial)
//    val uiState = _uiState.asStateFlow()


    private val _state = MutableStateFlow(ReceiveScreenState())
    val state : StateFlow<ReceiveScreenState> = combine(_state,_uiState){ state,uiState ->
        state.copy(
            uiState = uiState
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)



//    private val receivedPacketsChannel = Channel<DataPacket?>(capacity = UNLIMITED)
//    private val receivedPacketsFlow: Flow<DataPacket?> = receivedPacketsChannel.receiveAsFlow()


    //    val state : MutableStateFlow<ReceiveScreenState> = MutableStateFlow(
//        ReceiveScreenState()
//    )


    //TODO
//    val state: StateFlow<ReceiveScreenState> = combine<ReceiveUiState,ReceiveScreenState>(_uiState,_uiState2 ){}

    init {
        useCase.scope = viewModelScope
    }

    private var deviceConnectionJob: Job? = null


    fun onEvent(event: ReceiveScreenEvent) {
        when (event) {
            is ReceiveScreenEvent.SetAdvertising -> {
                if (event.active) startAdvertising()
                else stopAdvertising()
            }

            is ReceiveScreenEvent.ReactToOffer -> {
                if (event.accept) {
                    useCase.acceptOffer()
                } else {
                    useCase.rejectOffer()
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
            useCase.startAdvertising(
                registry = registry,
                duration = 10,
                callback = { resultDuration ->
                    resultDuration?.run {
                        _uiState.update { ReceiveUiState.Advertising }
//                        deviceConnectionJob?.cancel()
//                        deviceConnectionJob =
//                            p2pBluetoothConnection.startBluetoothServerAndGetFlow().listen()
                    }
                })
        }

    }

    //Due to a bug (?) in Android some devices will start advertising for 120s instead of 1s
    private fun stopAdvertising() {
        viewModelScope.launch {
            useCase.stopAdvertising(registry)
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
        //TODO
//        useCase.resetTransaction()
    }


//    private fun Flow<BluetoothConnectionStatus>.listen(): Job {
//        return onEach { result ->
//            when (result) {
//                is BluetoothConnectionStatus.ConnectionEstablished -> {
//                    _uiState.update {
//                        ReceiveUiState.Connected
//                    }
//                }
//
//                is BluetoothConnectionStatus.TransferSucceeded -> {
////                    receivedBluetoothPacketsChannel.send(result.bytes)
//                }
//
//                is BluetoothConnectionStatus.Error -> {}
//                BluetoothConnectionStatus.WaitingForConnection -> {}
//                BluetoothConnectionStatus.Disconnected -> {}
//                BluetoothConnectionStatus.NoConnection -> {}
//                BluetoothConnectionStatus.ConnectionInitiated -> {}
//                BluetoothConnectionStatus.Discovering -> {}
//            }
//        }.catch { throwable ->
//            p2pBluetoothConnection.closeConnection()
//            _uiState.update {
//                ReceiveUiState.Result(ReceiveUiState.ResultType.Failure("Exception: ${throwable.message}"))
//            }
//        }.launchIn(viewModelScope)
//    }


    override fun onCleared() {
        super.onCleared()
//        useCase.reset()
    }

    @AssistedFactory
    interface Factory {
        fun create(registry: ActivityResultRegistry): ReceiveViewModel
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