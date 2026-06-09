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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.core.view.WindowCompat

private val DarkColorScheme = lightColorScheme(
    primary = BloodRed,
    secondary = CrimsonRed,
    tertiary = DeepBurgundy,
    background = Color(0xFFFFFFFF),    // Pristine Pure White Background
    surface = Color(0xFFF9FAFB),       // Soft aesthetic off-white surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E1E1E),  // Dark text for super readability
    onSurface = Color(0xFF1E1E1E)
)

private val LightColorScheme = lightColorScheme(
    primary = BloodRed,
    secondary = CrimsonRed,
    tertiary = DeepBurgundy,
    background = Color(0xFFFFFFFF),    // Pristine Pure White Background
    surface = Color(0xFFF9FAFB),       // Soft aesthetic off-white surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E1E1E),  // Dark text for super readability
    onSurface = Color(0xFF1E1E1E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Force false by default to ensure app design is always bright and white as requested
    // Keep dynamic color toggleable but false by default to ensure blood donation red motif
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val view = LocalView.current
  if (!view.isInEditMode) {
      SideEffect {
          val window = (view.context as Activity).window
          window.statusBarColor = colorScheme.background.toArgb()
          window.navigationBarColor = colorScheme.surface.toArgb()
          
          val insetsController = WindowCompat.getInsetsController(window, view)
          // Make status bar icons dark on our white backgrounds so they are always visible
          insetsController.isAppearanceLightStatusBars = true
          insetsController.isAppearanceLightNavigationBars = true
      }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
