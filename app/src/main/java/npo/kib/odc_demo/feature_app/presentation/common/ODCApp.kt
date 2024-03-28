package npo.kib.odc_demo.feature_app.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCPlainBackground
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.common.components.backgroundHorizGradient
import npo.kib.odc_demo.feature_app.presentation.common.navigation.ODCNavHost
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun ODCApp(
    appState: ODCAppState = rememberODCAppState(/*windowSizeClass = windowSizeClass*/)
) {
    //val shouldShowSomeCustomBackground: Boolean
    ODCPlainBackground {
//        todo can use ConstraintLayout or BoxWithConstraints instead of the Scaffold
//        BoxWithConstraints(modifier = Modifier
//            .fillMaxSize()
//            /*.windowInsetsPadding(WindowInsets.systemBars)*//*.consumeWindowInsets(WindowInsets.systemBars)*/) {
//            val topBarHeight = maxHeight * 0.1f
//            val bottomBarHeight = maxHeight * 0.07f
//        }
        // https://developer.android.com/jetpack/compose/layouts/insets
        val systemNavHeight = (WindowInsets.systemBars.getBottom(
            LocalDensity.current
        ) / LocalDensity.current.density).dp
        Scaffold(containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (appState.shouldShowBottomBar) {
                    Column{
                        ODCBottomBar(
                            destinations = appState.topLevelDestinations,
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                            height = 55.dp
                        )
                        Box(
                            modifier = Modifier
                                .backgroundHorizGradient()
                                .fillMaxWidth()
                                .requiredHeight(systemNavHeight)
                        )
                    }
                }
            }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                ODCTopBar(modifier = Modifier.height(100.dp))
                ODCNavHost(
                    appState = appState, modifier = Modifier
                )
            }
        }
    }
}


@Preview
@Composable
private fun ODCAppPreview() {
    ODCAppTheme {
        ODCApp()
    }
}
