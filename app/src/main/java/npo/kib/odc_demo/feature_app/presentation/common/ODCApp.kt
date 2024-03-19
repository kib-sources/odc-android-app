package npo.kib.odc_demo.feature_app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCPlainBackground
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.common.navigation.ODCNavHost
import npo.kib.odc_demo.ui.theme.ODCAppTheme

@Composable
fun ODCApp(
    appState: ODCAppState = rememberODCAppState(/*windowSizeClass = windowSizeClass*/)
) {
    //val shouldShowSomeCustomBackground: Boolean
    ODCPlainBackground {
//        ODCGradientBackground {}
        BoxWithConstraints {
            val topBarHeight = maxHeight * 0.1f
            val bottomBarHeight = maxHeight * 0.07f
            //todo read here https://developer.android.com/jetpack/compose/layouts/insets
            Scaffold(containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    ODCTopBar(modifier = Modifier/*.height(topBarHeightPercentage)*/)
                },
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        Column(Modifier.height(bottomBarHeight + 35.dp)) {
                            ODCBottomBar(
                                destinations = appState.topLevelDestinations,
                                onNavigateToDestination = appState::navigateToTopLevelDestination,
                                currentDestination = appState.currentDestination,
                                modifier = Modifier.fillMaxHeight()
                            )
                            Box(
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.background)
                                    .fillMaxWidth()
                                    .requiredHeight(35.dp)
                            )
                        }
                    }
                }) { paddingValues ->
                ODCNavHost(
                    appState = appState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                        )
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
