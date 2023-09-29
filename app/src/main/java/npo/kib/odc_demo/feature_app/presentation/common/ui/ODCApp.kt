package npo.kib.odc_demo.feature_app.presentation.common.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCBottomBar
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCPlainBackground
import npo.kib.odc_demo.feature_app.presentation.common.ui.components.ODCTopBar
import npo.kib.odc_demo.feature_app.presentation.navigation.ODCNavHost

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ODCApp(
//  For later for adapting UI to different screen sizes --  windowSizeClass : WindowSizeClass,
    appState: ODCAppState = rememberODCAppState(/*windowSizeClass = windowSizeClass*/)
) {

    //val shouldShowSomeCustomBackground: Boolean

    ODCPlainBackground {
//        ODCGradientBackground {}
        BoxWithConstraints {
            val topBarHeightPercentage = maxHeight * 0.1f
            val bottomBarHeightPercentage = maxHeight * 0.11f

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

