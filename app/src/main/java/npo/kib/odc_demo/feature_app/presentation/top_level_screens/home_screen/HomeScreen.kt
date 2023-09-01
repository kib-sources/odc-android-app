package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.BalanceBlock
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.RoundedSquareButtonRow
import npo.kib.odc_demo.feature_app.presentation.navigation.TopLevelDestination
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier,
    navController: NavController? = null,
               onClickButton3 : () -> Unit = {},
    viewModelNew: HomeViewModelNew = hiltViewModel()
              ) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (balanceBlock, buttonRow) = createRefs()
        val balanceBlockHorGuideline = createGuidelineFromTop(0.2f)
        val buttonRowHorGuideline = createGuidelineFromTop(0.35f)
        val verticalCenterGuideline = createGuidelineFromStart(0.5f)
        BalanceBlock(modifier = Modifier.constrainAs(balanceBlock) {
            top.linkTo(balanceBlockHorGuideline)
            centerAround(verticalCenterGuideline)
        })
        RoundedSquareButtonRow(modifier = Modifier.constrainAs(buttonRow) {
            top.linkTo(buttonRowHorGuideline)
            centerAround(verticalCenterGuideline)
        }, onClickButton3)

    }
}

@Preview(showSystemUi = false)
@Composable
fun HomePreview() {
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