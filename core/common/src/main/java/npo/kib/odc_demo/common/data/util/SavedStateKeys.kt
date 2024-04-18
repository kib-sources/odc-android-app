package npo.kib.odc_demo.feature_app.data.util

/**For objects stored in SavedState and accessed with SavedStateHandle*/
sealed class SavedStateObject<T> {
    abstract val key: String
    abstract val defaultValue : T

    fun new(value : T) = value //for type-safety
}

sealed class SettingsValue<T>(override val key: String, override val defaultValue: T) : SavedStateObject<T>() {
    data object USER_NAME : SettingsValue<String>(key = "USER_NAME", defaultValue = "")
}