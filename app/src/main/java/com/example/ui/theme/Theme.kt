package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CloudHunterColorScheme = darkColorScheme(
  primary = AzureBlue,
  onPrimary = NavyDark,
  primaryContainer = NavyElevated,
  onPrimaryContainer = CyanAccent,
  secondary = CyanAccent,
  onSecondary = NavyDark,
  secondaryContainer = NavySurface,
  onSecondaryContainer = SkyBlue,
  tertiary = SkyBlue,
  onTertiary = NavyDark,
  background = NavyDark,
  onBackground = TextPrimary,
  surface = NavySurface,
  onSurface = TextPrimary,
  surfaceVariant = NavyElevated,
  onSurfaceVariant = TextSecondary,
  outline = NavyBorder,
  outlineVariant = NavyHighlight,
  error = DangerRed,
  onError = Color.White
)

@Composable
fun CloudHunterTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CloudHunterColorScheme,
    typography = Typography,
    content = content
  )
}
