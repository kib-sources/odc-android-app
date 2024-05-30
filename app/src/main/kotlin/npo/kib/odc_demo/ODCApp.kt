package npo.kib.odc_demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import npo.kib.odc_demo.core.design_system.components.ODCPlainBackground
import npo.kib.odc_demo.core.design_system.ui.DevicePreviews
import npo.kib.odc_demo.core.design_system.ui.ThemePreviews
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.feature.home.HomeScreenDefaultPreview
import npo.kib.odc_demo.navigation.ODCNavHost
import npo.kib.odc_demo.navigation.TopLevelDestination
import npo.kib.odc_demo.ui.components.*

@Composable
fun ODCApp(
    appState: ODCAppState
) {
    Box {
        val placeholderVisible = remember { MutableTransitionState(true) }
        var placeholderOnScreen by rememberSaveable { mutableStateOf(true) }
        if (placeholderOnScreen) {
            LaunchedEffect(key1 = Unit) {
                delay(100) //wait until initial screen renders and is shown fully
                placeholderVisible.targetState = false
                delay(500)
                placeholderOnScreen = false
            }
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .clickable(false) {},
                visibleState = placeholderVisible,
                enter = EnterTransition.None,
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(id = npo.kib.odc_demo.core.ui.R.drawable.logo_1),
                        contentDescription = null,
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix(
                                floatArrayOf(
                                    0.33f, 0.33f, 0.33f, 0f,
                                    0f, 0.33f, 0.33f, 0.33f,
                                    0f, 0f, 0.33f, 0.33f,
                                    0.33f, 0f, 0f, 0f,
                                    0f, 0f, 1f, 0f
                                ) //This matrix describes conversion to grayscale
                            )
                        )
                    )
                }
            }
        }

        ODCPlainBackground {
            val bottomSystemNavPadding =
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            Scaffold(containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ODCBottomBar(
                                modifier = Modifier
                                    .padding(bottom = bottomSystemNavPadding)
                                    .align(Alignment.TopCenter)
                                    .testBorder(Color.Magenta),
                                destinations = appState.topLevelDestinations,
                                onNavigateToDestination = appState::navigateToTopLevelDestination,
                                currentDestination = appState.currentDestination,
                                height = 60.dp
                            )
                            Box(
                                modifier = Modifier
                                    .backgroundHorizGradient()
                                    .fillMaxWidth()
                                    .requiredHeight(bottomSystemNavPadding)
                                    .align(Alignment.BottomCenter)
                                    .testBorder(Color.Cyan)
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues)
//                        .consumeWindowInsets(paddingValues)
//                        .safeDrawingPadding()
                ) {
//                    Box(
//                        modifier = Modifier
//                            .requiredHeight(systemTopBarWithCutoutHeight)
//                            .fillMaxWidth()
//                            .background(MaterialTheme.colorScheme.background)
//                            .testBorder(MaterialTheme.colorScheme.onBackground)
//                    )
                    Row(
                        Modifier
                            .fillMaxSize()
                            .testBorder(Color.Yellow),
                    ) {
                        if (appState.shouldShowNavRail) {
                            ODCNavRail(
                                destinations = appState.topLevelDestinations,
                                onNavigateToDestination = appState::navigateToTopLevelDestination,
                                currentDestination = appState.currentDestination,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues)
                                .safeDrawingPadding()
                                .testBorder(Color.Blue)
                        ) {
                            val destination = appState.currentTopLevelDestination
                            if (destination != null) ODCTopBar(
                                titleRes = destination.titleTextId,
                                modifier = Modifier
                                    .height(100.dp)
                                    .testBorder(Color.Green)
                            )
                            ODCNavHost(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testBorder(),
                                appState = appState
                            )
                        }
                    }
                }
            }
        }
    }
}
//}


// can set to true to see borders of all containers for testing
private fun Modifier.testBorder(color: Color = Color.Red) =
    if (false) this.border(
        1.dp,
        color
    ) else this

@ThemePreviews
@DevicePreviews
@Composable
private fun HomeScreenPreview() {
    ODCAppTheme {
        BoxWithConstraints(propagateMinConstraints = false) {
            val topBarHeightPercentage = maxHeight * 0.1f
            val topBarWithBalanceBlockHeightPercentage = maxHeight * 0.25f
            val bottomBarHeightPercentage = maxHeight * 0.07f


            Scaffold(modifier = Modifier.fillMaxSize(),
                topBar = {
                    ODCTopBar(
                        titleRes = android.R.string.untitled,
                        modifier = Modifier
                    )
                },
                bottomBar = {
                    ODCBottomBar(
                        destinations = TopLevelDestination.entries,
                        {},
                        currentDestination = null,
                        modifier = Modifier/*.height(bottomBarHeightPercentage)*/
                    )
                }) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
                    HomeScreenDefaultPreview()
                }
            }
        }
    }
}