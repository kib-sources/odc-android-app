package npo.kib.odc_demo.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import npo.kib.odc_demo.ui.theme.CustomColors

@Immutable
sealed class GradientColors(val color1: Color, val color2: Color, val container : Color = Color.Unspecified) {

    data object ColorSet1 : GradientColors(
        color1 = CustomColors.Gradient_Color_1,
        color2 = CustomColors.Gradient_Color_2
    )

    data object ColorSet2 : GradientColors(
        color1 = CustomColors.light_Gradient_Color_1,
        color2 = CustomColors.light_Gradient_Color_1Container
    )

}