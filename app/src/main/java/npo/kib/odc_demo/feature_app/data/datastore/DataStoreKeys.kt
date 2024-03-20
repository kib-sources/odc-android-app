package npo.kib.odc_demo.feature_app.data.datastore

import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.stringPreferencesKey

//todo maybe later migrate to Proto DataStore
sealed interface DataStoreKey<T> {
    val key: Key<T>
    val defaultValue: T?
}

/** Used for storing user-specific information that would affect UI and app state.
* */
sealed class DefaultDataStoreKey<T>(
    override val key: Key<T>,
    override val defaultValue: T? = null
) : DataStoreKey<T> {
    data object USER_NAME : DefaultDataStoreKey<String>(stringPreferencesKey("USER_NAME"), defaultValue = "Default User")

}



/** Used for storing internal utility information that would be used in business logic, like cached bluetooth name,
 * wallet-related open information. */
sealed class UtilityDataStoreKey<T>(
    override val key: Key<T>,
    override val defaultValue: T? = null
) : DataStoreKey<T> {
    data object BIN_KEY : UtilityDataStoreKey<String>(key = stringPreferencesKey("BIN"))
    data object BOK_KEY : UtilityDataStoreKey<String>(stringPreferencesKey("BOK"))
    data object SOK_SIGN_KEY : UtilityDataStoreKey<String>(stringPreferencesKey("SOK_signed"))
    data object WID_KEY : UtilityDataStoreKey<String>(stringPreferencesKey("WID"))
    data object CACHED_BLUETOOTH_NAME : DefaultDataStoreKey<String>(key = stringPreferencesKey("CACHED_BLUETOOTH_NAME"))

    //todo can add a key SHOULD_UPDATE_BALANCE for a Boolean to react to changes and trigger on 'true'
    // HomeViewModel.updateBalanceAndAppUserInfo()
}