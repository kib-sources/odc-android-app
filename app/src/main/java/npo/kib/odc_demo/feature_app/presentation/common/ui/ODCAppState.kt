package npo.kib.odc_demo.feature_app.presentation.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser


@Stable
data class ODCAppState(
    //Created on log in screen, need to set a default one temporarily
//    val currentAppUser : AppUser,
    val navController : NavHostController
                 ){
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination


//    private var currentAppUser : AppUser = AppUser()

}