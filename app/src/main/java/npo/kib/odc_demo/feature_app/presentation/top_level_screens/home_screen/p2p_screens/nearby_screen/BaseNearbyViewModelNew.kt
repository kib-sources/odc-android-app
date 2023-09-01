package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen.p2p_screens.nearby_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BaseNearbyViewModelNew @Inject constructor() : ViewModel() {


}

sealed interface BaseP2PUiState {
    object Searching : BaseP2PUiState
    object Connected : BaseP2PUiState
    object Interrupted : BaseP2PUiState
    object Accepted : BaseP2PUiState
    object Rejected : BaseP2PUiState
    object Processing : BaseP2PUiState
    object Success : BaseP2PUiState
}