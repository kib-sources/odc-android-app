package npo.kib.odc_demo.core.datastore.model

import npo.kib.odc_demo.core.datastore.DefaultDataStoreObject.USER_NAME


/**
 * Class representing user preferences and saved information
 * */
//@Parcelize
data class UserPreferences(
    val userName: String = USER_NAME.defaultValue,
    //todo add photo, app settings parameters, etc. later
) /*: Parcelable*/