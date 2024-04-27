package npo.kib.odc_demo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import npo.kib.odc_demo.core.design_system.ui.GradientColors

/**
 * ODC navigation default values (colors).
 */
object OdcNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun navigationGradientColors() = GradientColors.ColorSet1

}