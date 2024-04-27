package npo.kib.odc_demo.datastore


import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import npo.kib.odc_demo.datastore.UtilityDataStoreObject.SHOULD_UPDATE_UI_USER_INFO

sealed interface DataStoreObject<T> {
    val key: Key<T>
    val defaultValue: T?
}

/** Used for storing user-specific information that would affect UI and app state.
 * */
sealed class DefaultDataStoreObject<T>(
    override val key: Key<T>, override val defaultValue: T
//todo can add property for insertion formatting rule (lambda).
// Booleans will have empty formatting lambda, strings will get formated according to project rules.
// Upon insertion, every key will have the formatting lambda applied to. The empty lambdas will not have effect.
) : DataStoreObject<T> {
    data object USER_NAME : DefaultDataStoreObject<String>(
        stringPreferencesKey("USER_NAME"), defaultValue = "Default"
    )
}

/** Used for storing internal utility information that would be used in business logic, like cached bluetooth name,
 * wallet-related open information. */
sealed class UtilityDataStoreObject<T>(
    override val key: Key<T>, override val defaultValue: T
) : DataStoreObject<T> {
    data object BIN : UtilityDataStoreObject<String>(key = stringPreferencesKey("BIN"), "")
    data object BOK : UtilityDataStoreObject<String>(stringPreferencesKey("BOK"), "")
    data object SOK_SIGN : UtilityDataStoreObject<String>(stringPreferencesKey("SOK_signed"), "")
    data object WID : UtilityDataStoreObject<String>(stringPreferencesKey("WID"), "")
    data object CACHED_BLUETOOTH_NAME :
        UtilityDataStoreObject<String>(key = stringPreferencesKey("CACHED_BLUETOOTH_NAME"), "")

    data object IS_BLUETOOTH_NAME_CHANGED : UtilityDataStoreObject<Boolean>(
        key = booleanPreferencesKey("IS_BLUETOOTH_NAME_CHANGED"), false
    )

    data object SHOULD_UPDATE_UI_USER_INFO : UtilityDataStoreObject<Boolean>(
        key = booleanPreferencesKey("SHOULD_UPDATE_UI_USER_INFO"), false
    )
}

data class PublicUtilData(
    val shouldUpdateUiUserInfo: Boolean = SHOULD_UPDATE_UI_USER_INFO.defaultValue
)