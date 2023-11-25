package npo.kib.odc_demo.feature_app.data.datastore

import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.stringPreferencesKey

sealed interface DataStoreKey<T> {
    val it: Key<T>
    val defaultValue: T?
}

sealed class DefaultDataStoreKey<T>(
    override val it: Key<T>,
    override val defaultValue: T? = null
) : DataStoreKey<T> {
    data object USER_NAME : DefaultDataStoreKey<String>(stringPreferencesKey("USER_NAME"))
    data object CACHED_BLUETOOTH_NAME : DefaultDataStoreKey<String>(it = stringPreferencesKey("CACHED_BLUETOOTH_NAME"))
}

sealed class KeysDataStoreKey<T>(
    override val it: Key<T>,
    override val defaultValue: T? = null
) : DataStoreKey<T> {
    data object BIN_KEY : KeysDataStoreKey<String>(it = stringPreferencesKey("BIN"))
    data object BOK_KEY : KeysDataStoreKey<String>(stringPreferencesKey("BOK"))
    data object SOK_SIGN_KEY : KeysDataStoreKey<String>(stringPreferencesKey("SOK_signed"))
    data object WID_KEY : KeysDataStoreKey<String>(stringPreferencesKey("WID"))
}