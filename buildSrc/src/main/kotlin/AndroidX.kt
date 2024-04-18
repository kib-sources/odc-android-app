object AndroidX {

    const val lifecycleVersion = "2.7.0"

//    const val lifecycleViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
    const val lifecycleViewmodelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion"

    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion"
    //for the collectAsStateWithLifecycle()
    const val lifecycleRuntimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion"

    private const val lifecycleExtensionsVersion = "2.2.0"
    const val lifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:$lifecycleExtensionsVersion"

    const val legacySupport = "androidx.legacy:legacy-support-v13:1.0.0"

    private const val coreKtxVersion = "1.10.1"
    const val coreKtx = "androidx.core:core-ktx:$coreKtxVersion"

//    private const val appCompatVersion = "1.6.1"
    private const val appCompatVersion = "1.2.0"
    const val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"

    private const val constraintLayoutVer = "2.2.0-alpha10"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVer"


    //added for the enableEdgeToEdge() essentially, replacement for https://google.github.io/accompanist/systemuicontroller
    private const val activityVer = "1.8.0-rc01"
    const val activity = "androidx.activity:activity:$activityVer"

    private const val traceVer = "1.2.0"
    const val tracingKtx = "androidx.tracing:tracing-ktx:$traceVer"


    private const val preferenceKtxVersion = "1.2.0"
    const val preferenceKtx = "androidx.preference:preference-ktx:$preferenceKtxVersion"

    private const val datastorePreferencesVer = "1.1.0-alpha06"
    const val datastorePreferences = "androidx.datastore:datastore-preferences:$datastorePreferencesVer"

    private const val splashScreenVer = "1.0.1"
    const val splashScreen = "androidx.core:core-splashscreen:$splashScreenVer"

}