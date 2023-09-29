package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.RoundedSquareButtonRow
import npo.kib.odc_demo.ui.theme.ODCAppTheme


@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               navController: NavController? = null,
               onClickButton3: () -> Unit = {},
               viewModelNew: HomeViewModelNew = hiltViewModel()
              ) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (buttonRow) = createRefs()
        val buttonRowHorGuideline = createGuidelineFromTop(0.35f)
        val verticalCenterGuideline = createGuidelineFromStart(0.5f)
        //balance block should be common for all main app feature screens
//        //todo pass AppUser
//        BalanceBlock(modifier = Modifier.constrainAs(balanceBlock) {
//            top.linkTo(balanceBlockHorGuideline)
//            centerAround(verticalCenterGuideline)
//        })

        //Here go either a button row or p2p screens in a separate container, the balance block remains visible this way
        //on the Home screen and child screens but not on any others, like Settings, History or Log-in screens.
        //@Composable P2PContainer
        RoundedSquareButtonRow(modifier = Modifier.constrainAs(buttonRow) {
            top.linkTo(buttonRowHorGuideline)
            centerAround(verticalCenterGuideline)
        }, onClickButton3)

    }
}

@Preview(showSystemUi = false)
@Composable
private fun HomePreview() {
    ODCAppTheme {
        HomeScreen()
    }
}

//See reference in nowinandroid
//fun NavController.navigateToHome(navOptions: NavOptions? = null) {
//    this.navigate(TopLevelDestination.HOME.route, navOptions)
//}
//
//fun NavGraphBuilder.homeScreen() {
//    composable(route = TopLevelDestination.HOME.route) {
//        HomeRoute()
//    }
//}