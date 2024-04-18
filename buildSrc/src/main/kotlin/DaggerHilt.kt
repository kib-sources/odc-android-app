object DaggerHilt {
    const val version = "2.50"
    const val hiltAndroid = "com.google.dagger:hilt-android:$version"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:$version"

    const val hiltPlugin = "com.google.dagger.hilt.android"


    private const val hiltNavComposeVersion = "1.2.0" //1.0.0 -- latest stable
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:$hiltNavComposeVersion"

}