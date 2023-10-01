package npo.kib.odc_demo.feature_app.presentation.p2p_screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import npo.kib.odc_demo.feature_app.domain.model.user.AppUser

@Composable
fun rememberP2PCommonState(/*todo pass ODCAppState here and take some props and store them in P2PCommonState, like AppUser*/
                           navController: NavHostController = rememberNavController()
): P2PCommonState {
    return remember(key1 = navController) {
        P2PCommonState(navController)
    }
}

@Stable
class P2PCommonState(/*todo pass ODCAppState here and do something in the init block*/
                     val navController: NavHostController
) {
    private lateinit var currentUser: AppUser

    init {}

}