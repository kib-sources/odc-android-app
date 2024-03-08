package npo.kib.odc_demo.feature_app.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCPlainBackground
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.common.navigation.ODCNavHost

@Composable
fun ODCApp(
    appState: ODCAppState = rememberODCAppState(/*windowSizeClass = windowSizeClass*/)
) {
    //val shouldShowSomeCustomBackground: Boolean
    ODCPlainBackground {
//        ODCGradientBackground {}
        BoxWithConstraints {
            val topBarHeightPercentage = maxHeight * 0.1f
            val bottomBarHeightPercentage = maxHeight * 0.11f
            //fixme
            //todo read here https://developer.android.com/jetpack/compose/layouts/insets
            // understand insets, and adapt the bottom bar size correctly.
            // Need to have some background behind the system navigation, which will
            // adapt if it collapses (gesture navigation)
            Scaffold(containerColor = Color.Transparent,
                     contentColor = MaterialTheme.colorScheme.onBackground,
                     contentWindowInsets = WindowInsets(0, 0, 0, 0),
                     topBar = {
                         ODCTopBar(modifier = Modifier/*.height(topBarHeightPercentage)*/)
                     },
                     bottomBar = {
                         if (appState.shouldShowBottomBar) {
                             ODCBottomBar(
                                 destinations = appState.topLevelDestinations,
                                 onNavigateToDestination = appState::navigateToTopLevelDestination,
                                 currentDestination = appState.currentDestination,
                                 modifier = Modifier.height(bottomBarHeightPercentage)
                             )
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

