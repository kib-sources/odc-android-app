package npo.kib.odc_demo.feature_app.data.datastore

import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.stringPreferencesKey

//todo maybe later migrate to Proto DataStore
sealed interface DataStoreKey<T> {
    val key: Key<T>
    val defaultValue: T?
}

sealed class DefaultDataStoreKey<T>(
    override val key: Key<T>,
    override val defaultValue: T? = null
) : DataStoreKey<T> {
    data object USER_NAME : DefaultDataStoreKey<String>(stringPreferencesKey("USER_NAME"))
    data object CACHED_BLUETOOTH_NAME : DefaultDataStoreKey<String>(key = stringPreferencesKey("CACHED_BLUETOOTH_NAME"))
}

sealed class KeysDataStoreKey<T>(
    override val key: Key<T>,
    override val defaultValue: T? = null
) : DataStoreKey<T> {
    data object BIN_KEY : KeysDataStoreKey<String>(key = stringPreferencesKey("BIN"))
    data object BOK_KEY : KeysDataStoreKey<String>(stringPreferencesKey("BOK"))
    data object SOK_SIGN_KEY : KeysDataStoreKey<String>(stringPreferencesKey("SOK_signed"))
    data object WID_KEY : KeysDataStoreKey<String>(stringPreferencesKey("WID"))
}