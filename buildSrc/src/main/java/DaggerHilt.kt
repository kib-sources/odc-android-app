object DaggerHilt {
    const val version = "2.47"
    const val hiltAndroid = "com.google.dagger:hilt-android:$version"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:$version"
    private const val hiltNavComposeVersion = "1.1.0-alpha01" //1.0.0 -- latest stable
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:$hiltNavComposeVersion"
}