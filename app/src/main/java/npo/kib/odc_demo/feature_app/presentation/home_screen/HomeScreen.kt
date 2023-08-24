package npo.kib.odc_demo.feature_app.presentation.home_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.AppTopBar
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.BalanceBlock
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.RSBPreview
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
        RSBPreview(modifier = Modifier.constrainAs(buttonRow) {
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
