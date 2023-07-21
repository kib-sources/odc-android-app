object Compose {
    //https://developer.android.com/jetpack/compose/bom/bom-mapping
    private const val composeBOMVersion = "2023.06.01"
    const val composeBOM = "androidx.compose:compose-bom:$composeBOMVersion"

    const val ui = "androidx.compose.ui:ui"
    const val uiTooling = "androidx.compose.ui:ui-tooling"
    const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val animation = "androidx.compose.animation:animation"

    const val material2 = "androidx.compose.material:material"
    const val material3 = "androidx.compose.material3:material3"

    private const val activityComposeVer = "1.7.2"
    const val activity = "androidx.activity:activity-compose:$activityComposeVer"

    private const val navigationVersion = "2.6.0"
    const val navigation = "androidx.navigation:navigation-compose:$navigationVersion"

    //Might use later, added in advance
    private const val constraintLayoutComposeVer = "1.1.0-alpha10"
    const val constraintLayoutCompose =
        "androidx.constraintlayout:constraintlayout-compose:$constraintLayoutComposeVer"
}