package npo.kib.odc_demo.core.design_system.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ProgressBar() {

}

@Composable
fun LoadingAnimation() {
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
@Composable
fun StatusInfoBlock(
    modifier: Modifier = Modifier,
    statusLabel: String,
    infoText: String? = null
) {
    Column(modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = statusLabel,
            modifier = Modifier.animateFadeVerticalSlideInOut()
        )
        infoText?.let {
            Text(
                text = it,
                modifier = Modifier.animateFadeVerticalSlideInOut(enterDelay = 100)
            )
        }
    }
}


context(AnimatedVisibilityScope)
@OptIn(ExperimentalAnimationApi::class)
fun Modifier.animateFadeVerticalSlideInOut(
    initialOffsetY: (Int) -> Int = { -it },
    targetOffsetY: (Int) -> Int = { -it },
    enterDuration: Int = 1000,
    exitDuration: Int = 1000,
    enterDelay: Int = 0,
    exitDelay: Int = 0
): Modifier = animateEnterExit(
    enter = fadeIn(
        tween(
            durationMillis = enterDuration, delayMillis = enterDelay
        )
    ) + slideInVertically(
        initialOffsetY = initialOffsetY,
        animationSpec = tween(durationMillis = enterDuration, delayMillis = enterDelay)
    ), exit = fadeOut(
        tween(
            durationMillis = exitDuration, delayMillis = exitDelay
        )
    ) + slideOutVertically(
        targetOffsetY = targetOffsetY,
        animationSpec = tween(durationMillis = exitDuration, delayMillis = exitDelay)
    )
)

context(AnimatedVisibilityScope)
@OptIn(ExperimentalAnimationApi::class)
fun Modifier.animateVerticalSlideInOut(
    initialOffsetY: (Int) -> Int = { -it },
    targetOffsetY: (Int) -> Int = { -it },
    enterDuration: Int = 1000,
    exitDuration: Int = 1000,
    enterDelay: Int = 0,
    exitDelay: Int = 0
): Modifier = animateEnterExit(
    enter = slideInVertically(
        initialOffsetY = initialOffsetY,
        animationSpec = tween(durationMillis = enterDuration, delayMillis = enterDelay)
    ), exit = slideOutVertically(
        targetOffsetY = targetOffsetY,
        animationSpec = tween(durationMillis = exitDuration, delayMillis = exitDelay)
    )
)

context(AnimatedVisibilityScope)
@OptIn(ExperimentalAnimationApi::class)
fun Modifier.animateFadeInOut(
    enterDuration: Int = 1000, exitDuration: Int = 1000, enterDelay: Int = 0, exitDelay: Int = 0
): Modifier = animateEnterExit(
    enter = fadeIn(
        tween(durationMillis = enterDuration, delayMillis = enterDelay)
    ), exit = fadeOut(tween(durationMillis = exitDuration, delayMillis = exitDelay))
)


fun Modifier.rotating(
    duration: Int = 5000,
    isClockwise: Boolean = true,
    easing: Easing = LinearEasing
): Modifier = this.composed {
    val transition = rememberInfiniteTransition(label = "")
    val angleRatio by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(duration, easing = easing)),
        label = ""
    )
    graphicsLayer(
        rotationZ = 360f * angleRatio * if (isClockwise) 1 else -1
    )
}

@Preview
@Composable
private fun RotatingPreview() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.Center)
                .size(150.dp)
                .rotating()
                .background(Color.Red)
        )
    }
}

@Preview
@Composable
private fun ControllableRotatingPreview() {
    var rotating by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        Button(onClick = { rotating = !rotating }) {
            Text(text = "${if (rotating) "Stop" else "Start"} rotating")
        }
        Box(
            Modifier
                .align(Alignment.Center)
                .size(150.dp)
                .rotatingOnCondition(rotating, 4000, true)
                .background(Color.Red)
        )
    }
}

fun Modifier.rotatingOnCondition(
    isRotating: Boolean,
    duration: Int = 5000,
    isClockwise: Boolean = true,
    easing: Easing = LinearEasing
): Modifier = composed {
    val angle = remember { Animatable(0f) }
    // Handle the initiation and termination of animation based on isAnimating boolean
    LaunchedEffect(isRotating) {
        if (isRotating) {
            // Animate indefinitely as long as isAnimating is true
            angle.animateTo(
                // Using a target value of 360 for a full rotation
                targetValue = 360f,
                // Restart the animation each time it completes
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = duration, easing = easing),
                    // Restart immediately without delay
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            // If isAnimating becomes false, stop the animation
            angle.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    graphicsLayer {
        // Apply the rotation angle
        rotationZ = angle.value * if (isClockwise) 1 else -1
    }
}