package npo.kib.odc_demo.feature_app.presentation.top_level_screens.settings_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import npo.kib.odc_demo.feature_app.data.datastore.DefaultDataStoreObject
import npo.kib.odc_demo.feature_app.data.util.SettingsValue.USER_NAME
import npo.kib.odc_demo.feature_app.domain.repository.DefaultDataStoreRepository
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val defaultDatastore: DefaultDataStoreRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val savedName = defaultDatastore.userPreferencesFlow.map { it.userName }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(1000), ""
    )

    private val enteredName = savedStateHandle.getStateFlow(USER_NAME.key, savedName.value)

    private val canSaveNow: StateFlow<Boolean> =
        combine(savedName, enteredName) { saved, entered -> saved != entered && entered.isNotBlank() }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(2000), false
        )

    val settingsState = savedName.combine(canSaveNow) { name, b ->
        SettingsState(userName = name, isSaveButtonActive = b)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(2000), SettingsState()
    )

    //todo use this and a parcelable class for all data when there are more fields
//    fun <T> onPropertyChanged(key: SettingsValue<T>, newValue: T) {
//        savedStateHandle[key.key] = newValue
//    }

//todo use UserPreferences parcelable instead of user name string when adding more fields
//    can save complex parcelable objects in saved instance state https://developer.android.com/topic/libraries/architecture/saving-states
//     https://developer.android.com/reference/android/os/Bundle Bundle is "A mapping from String keys to various Parcelable values."
    fun saveChanges() {
        viewModelScope.launch {
            defaultDatastore.writeValue(DefaultDataStoreObject.USER_NAME, enteredName.value.trim())
        }
    }

    fun onNameEntered(newValue: String) {
        savedStateHandle[USER_NAME.key] = USER_NAME.new(newValue)
    }

}

data class SettingsState(val userName: String = "", val isSaveButtonActive: Boolean = false)