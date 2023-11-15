package npo.kib.odc_demo.feature_app.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import npo.kib.odc_demo.feature_app.domain.repository.DatastorePreferencesRepository
import javax.inject.Inject

class DatastorePreferencesRepositoryImpl @Inject constructor(private val datastore: DataStore<Preferences>) : DatastorePreferencesRepository {

    fun read(){

    }

// https://developer.android.com/topic/libraries/architecture/datastore
    fun write(){


    }


}