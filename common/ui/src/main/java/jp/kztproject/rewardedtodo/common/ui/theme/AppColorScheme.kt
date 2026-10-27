package jp.kztproject.rewardedtodo.common.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RewardedTodoScheme(isDarkTheme: Boolean) = if (isDarkTheme) {
    darkColorScheme(
        primary = Color(0xFFFF7A3D),
        onPrimary = Color(0xFF3A1B05),
        secondary = Color(0xFFFFC944),
        onSecondary = Color(0xFF352600),
        secondaryContainer = Color(0xFF262A39),
        onSecondaryContainer = Color(0xFFFFC944),
        tertiary = Color(0xFF34D88E),
        background = Color(0xFF13141C),
        onBackground = Color(0xFFF3F4FA),
        surface = Color(0xFF1C1E29),
        onSurface = Color(0xFFF3F4FA),
        surfaceVariant = Color(0xFF262A39),
        onSurfaceVariant = Color(0xFFB4B8CA),
        outlineVariant = Color(0xFF3A3D4A),
    )
} else {
    lightColorScheme(
        primary = Color(0xFFFF7A3D),
        onPrimary = Color(0xFF3A1B05),
        secondary = Color(0xFF9A5800),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFF2E1),
        onSecondaryContainer = Color(0xFF9A5800),
        tertiary = Color(0xFF1B8653),
        background = Color(0xFFFBF6EF),
        onBackground = Color(0xFF201C16),
        surface = Color.White,
        onSurface = Color(0xFF201C16),
        surfaceVariant = Color(0xFFF6EFE5),
        onSurfaceVariant = Color(0xFF625B50),
        outlineVariant = Color(0xFFE7DED2),
    )
}
