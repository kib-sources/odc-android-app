package npo.kib.odc_demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import npo.kib.odc_demo.core.design_system.ui.DevicePreviews
import npo.kib.odc_demo.core.design_system.ui.ThemePreviews
import npo.kib.odc_demo.core.design_system.ui.theme.ODCAppTheme
import npo.kib.odc_demo.home.HomeScreenDefaultPreview
import npo.kib.odc_demo.navigation.ODCNavHost
import npo.kib.odc_demo.navigation.TopLevelDestination
import npo.kib.odc_demo.ui.components.ODCBottomBar
import npo.kib.odc_demo.ui.components.ODCNavRail
import npo.kib.odc_demo.ui.components.ODCTopBar
import npo.kib.odc_demo.ui.components.backgroundHorizGradient

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
                                    0.33f,
                                    0.33f,
                                    0.33f,
                                    0f,
                                    0f,
                                    0.33f,
                                    0.33f,
                                    0.33f,
                                    0f,
                                    0f,
                                    0.33f,
                                    0.33f,
                                    0.33f,
                                    0f,
                                    0f,
                                    0f,
                                    0f,
                                    0f,
                                    1f,
                                    0f
                                ) //Should allow to convert to grayscale
                            )
                        )
                    )
                }
            }
        }

        //val shouldShowSomeCustomBackground: Boolean
        npo.kib.odc_demo.core.design_system.components.ODCPlainBackground {
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
                        Column {
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
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal,
                            ),
                        ),
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
                    ) {
                        val destination = appState.currentTopLevelDestination
                        val shouldShowTopAppBar = destination != null
                        if (destination != null) ODCTopBar(
                            titleRes = destination.titleTextId, modifier = Modifier.height(100.dp)
                        )
                        ODCNavHost(appState = appState)
                    }
                }
            }
        }
    }
}


@ThemePreviews
@DevicePreviews
@Composable
private fun HomeScreenPreview() {
    ODCAppTheme {
        BoxWithConstraints(propagateMinConstraints = false) {
            val topBarHeightPercentage = maxHeight * 0.1f
            val topBarWithBalanceBlockHeightPercentage = maxHeight * 0.25f
            val bottomBarHeightPercentage = maxHeight * 0.07f


            Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                ODCTopBar(titleRes = android.R.string.untitled, modifier = Modifier)
            }, bottomBar = {
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