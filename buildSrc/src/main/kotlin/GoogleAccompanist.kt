// https://mvnrepository.com/artifact/com.google.accompanist
object GoogleAccompanist {

//    We can remove accompanist/systemuicontroller as a dependency, since the only usage of it in this project is superceded by Activity.enableEdgeToEdge
//    rememberSystemUiController() is deprecated
//    private const val uiControllerVersion = "0.33.2-alpha"
//    const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:$uiControllerVersion"

    //SwipeRefreshLayout

    //Permissions in Compose
    private const val permissionsVer = "0.32.0"
    const val permissions = "com.google.accompanist:accompanist-permissions:$permissionsVer"

}