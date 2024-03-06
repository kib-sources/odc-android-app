package npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AdvertisingAnimation() {

}

@Composable
fun SearchingAnimation() {

}

@Composable
fun ProgressBar() {

}

@Composable
fun LoadingAnimation() {
    CircularProgressIndicator()
}

//todo add 3 dots infinite loading animation

@Composable
fun FadeInOutBlock(
    visibleState: MutableTransitionState<Boolean>,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
        exit = fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        content()
    }
}

context(AnimatedVisibilityScope)
@OptIn(ExperimentalAnimationApi::class)
fun Modifier.animateFadeVerticalSlideInOut(
    initialOffsetY: (Int) -> Int = { it },
    targetOffsetY: (Int) -> Int = { it },
    enterDuration : Int = 1500,
    exitDuration : Int = 1500,
    enterDelay : Int = 0,
    exitDelay : Int = 0
): Modifier = animateEnterExit(
    enter = fadeIn(tween(durationMillis = enterDuration, delayMillis = enterDelay)) + slideInVertically(
        initialOffsetY = initialOffsetY,
        animationSpec = tween(durationMillis = enterDuration, delayMillis = enterDelay)
    ),
    exit = fadeOut(tween(durationMillis = exitDuration, delayMillis = exitDelay)) + slideOutVertically(
        targetOffsetY = targetOffsetY,
        animationSpec = tween(durationMillis = exitDuration, delayMillis = exitDelay)
    )
)

context(AnimatedVisibilityScope)
@OptIn(ExperimentalAnimationApi::class)
fun Modifier.animateFadeInOut(
    enterDuration : Int = 1500,
    exitDuration : Int = 1500,
    enterDelay : Int = 0,
    exitDelay : Int = 0
): Modifier = animateEnterExit(
    enter = fadeIn(tween(durationMillis = enterDuration, delayMillis = enterDelay)
    ),
    exit = fadeOut(tween(durationMillis = exitDuration, delayMillis = exitDelay))
)

