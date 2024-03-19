package npo.kib.odc_demo.feature_app.presentation.p2p_screens.atm_screen

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import npo.kib.odc_demo.feature_app.domain.util.isAValidAmount
import npo.kib.odc_demo.feature_app.presentation.common.components.ODCGradientActionButton
import npo.kib.odc_demo.feature_app.presentation.common.components.TransparentHintTextField
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.StatusInfoBlock
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.animateFadeVerticalSlideInOut
import npo.kib.odc_demo.feature_app.presentation.p2p_screens.common.components.animateVerticalSlideInOut
import npo.kib.odc_demo.ui.GradientColors

@Composable
fun ATMRoute(
    navigateToP2PRoot: () -> Unit, navBackStackEntry: NavBackStackEntry
) {
    val viewModel = hiltViewModel<ATMViewModelNew>(viewModelStoreOwner = navBackStackEntry)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ATMScreen(
        uiState = uiState, navigateToP2PRoot = navigateToP2PRoot, onEvent = viewModel::onEvent
    )
}

@Composable
fun ATMScreen(
    uiState: ATMUiState, navigateToP2PRoot: () -> Unit, onEvent: (ATMScreenEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val areScreenLabelsVisible =
            remember { MutableTransitionState(false) }.apply { targetState = true }
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(50.dp),
            visibleState = areScreenLabelsVisible,
        ) {
            Text(
                text = "Request banknotes from server on this screen",
                modifier = Modifier
                    .fillMaxHeight()
                    .animateVerticalSlideInOut(),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = uiState,
            contentKey = { uiState },
            label = "ATMScreenAnimatedContent",
            transitionSpec = {
                fadeIn(tween(1000, 0)) togetherWith fadeOut(tween(1000, 0))
            },
            contentAlignment = Alignment.Center,
        ) { state ->
            when (state) {
                ATMUiState.Initial -> AtmScreenSubScreens.InitialScreen(onEvent = onEvent)
                ATMUiState.Waiting -> StatusInfoBlock(statusLabel = "Waiting for result")
                is ATMUiState.Result -> when (state.result) {
                    is ATMUiState.ResultType.Failure -> StatusInfoBlock(
                            statusLabel = "Failed with an error:",
                            infoText = state.result.failureMessage
                        )
                    ATMUiState.ResultType.Success -> StatusInfoBlock(statusLabel = "Received successfully!")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(
            modifier = Modifier
                .requiredHeight(40.dp),
            visibleState = areScreenLabelsVisible,
        ) {
            Text(text = "Back",
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { navigateToP2PRoot() }
                    .animateFadeVerticalSlideInOut(),
                textAlign = TextAlign.Center)

        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

private object AtmScreenSubScreens {

    @Composable
    fun InitialScreen(
        onEvent: (ATMScreenEvent) -> Unit
    ) {
        var amountText by rememberSaveable {
            mutableStateOf("")
        }
        val amountIsValid by remember {
            derivedStateOf { amountText.isAValidAmount() }
        }
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransparentHintTextField(modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    Dp.Hairline, color = MaterialTheme.colorScheme.onSurface
                )
                .padding(15.dp),
                hint = "Enter amount to request...",
                onValueChange = { amountText = it })
            Spacer(modifier = Modifier.height(10.dp))
            ODCGradientActionButton(text = if (amountIsValid) "Send request" else "Invalid amount",
                enabled = amountIsValid,
                gradientColors = if (amountIsValid) GradientColors.ButtonPositiveActionColors else GradientColors.ButtonNegativeActionColors,
                onClick = {
                    if (amountIsValid) onEvent(ATMScreenEvent.SendAmountRequestToServer(amountText.toInt()))
                })
        }
    }


}