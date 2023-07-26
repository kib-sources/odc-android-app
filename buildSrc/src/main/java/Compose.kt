object Compose {
    const val kotlinCompilerExtensionVersion = "1.4.7"
    //https://developer.android.com/jetpack/compose/bom/bom-mapping
    private const val composeBOMVersion = "2023.06.01"
    const val composeBOM = "androidx.compose:compose-bom:$composeBOMVersion"

    const val ui = "androidx.compose.ui:ui"
    const val uiTooling = "androidx.compose.ui:ui-tooling"
    const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val animation = "androidx.compose.animation:animation"
    const val runtime = "androidx.compose.runtime:runtime"



    const val material2 = "androidx.compose.material:material"
    const val material3 = "androidx.compose.material3:material3"

    private const val activityComposeVer = "1.7.2"
    const val activityCompose = "androidx.activity:activity-compose:$activityComposeVer"

    private const val lifecycleVersion = "2.6.1"
    const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion"

    private const val navigationVersion = "2.6.0"
    const val navigation = "androidx.navigation:navigation-compose:$navigationVersion"

    private const val hiltNavigationComposeVersion = "2.6.0"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion"


    //Might use later, added in advance
    private const val constraintLayoutComposeVer = "1.1.0-alpha10"
    const val constraintLayoutCompose =
        "androidx.constraintlayout:constraintlayout-compose:$constraintLayoutComposeVer"
}