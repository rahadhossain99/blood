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

private val DarkColorScheme = darkColorScheme(
    primary = LightBloodRose,
    secondary = RoseRedSecondary,
    tertiary = SoftBurgundy,
    background = HeavyBurgundy,
    surface = CocoaSurface,
    onPrimary = Color(0xFF6E000B),
    onSecondary = Color.White,
    onBackground = Color(0xFFFFEDED),
    onSurface = Color(0xFFFFEDED)
)

private val LightColorScheme = lightColorScheme(
    primary = BloodRed,
    secondary = CrimsonRed,
    tertiary = DeepBurgundy,
    background = CleanCream,
    surface = WarmRose,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF351F20),
    onSurface = Color(0xFF351F20)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
