package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = CoachBluePrimaryDark,
  onPrimary = CoachBlueOnPrimaryDark,
  primaryContainer = CoachBlueContainerDark,
  onPrimaryContainer = CoachBlueOnContainerDark,
  secondary = CoachTealSecondaryDark,
  onSecondary = CoachTealOnSecondaryDark,
  secondaryContainer = CoachTealContainerDark,
  onSecondaryContainer = CoachTealOnContainerDark,
  tertiary = CoachAmberTertiaryDark,
  onTertiary = CoachAmberOnTertiaryDark,
  tertiaryContainer = CoachAmberContainerDark,
  onTertiaryContainer = CoachAmberOnContainerDark,
  background = BackgroundDark,
  onBackground = OnBackgroundDark,
  surface = SurfaceDark,
  onSurface = OnSurfaceDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = OnSurfaceVariantDark,
  outline = OutlineDark,
)

private val LightColorScheme = lightColorScheme(
  primary = CoachBluePrimary,
  onPrimary = CoachBlueOnPrimary,
  primaryContainer = CoachBlueContainer,
  onPrimaryContainer = CoachBlueOnContainer,
  secondary = CoachTealSecondary,
  onSecondary = CoachTealOnSecondary,
  secondaryContainer = CoachTealContainer,
  onSecondaryContainer = CoachTealOnContainer,
  tertiary = CoachAmberTertiary,
  onTertiary = CoachAmberOnTertiary,
  tertiaryContainer = CoachAmberContainer,
  onTertiaryContainer = CoachAmberOnContainer,
  background = BackgroundLight,
  onBackground = OnBackgroundLight,
  surface = SurfaceLight,
  onSurface = OnSurfaceLight,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = OnSurfaceVariantLight,
  outline = OutlineLight,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding colors for coaching log
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
