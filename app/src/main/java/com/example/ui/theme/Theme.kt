package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = Color(0xFF031E2A),
    primaryContainer = Color(0xFF004D5C),
    onPrimaryContainer = Color(0xFF9CF4FF),

    secondary = BlueLaser,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF003666),
    onSecondaryContainer = Color(0xFFBCE0FD),

    tertiary = EmeraldTelemetry,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),

    background = TechNavy900,
    onBackground = TextHighEmphasis,

    surface = TechNavy800,
    onSurface = TextHighEmphasis,

    surfaceVariant = TechNavy700,
    onSurfaceVariant = TextMediumEmphasis,

    outline = TechNavy600,
    outlineVariant = TechNavy500,

    error = RedHazard,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme( // We prefer a sleek dark aesthetic for Edge AI & Telemetry
    primary = CyanNeon,
    onPrimary = Color(0xFF031E2A),
    primaryContainer = Color(0xFF004D5C),
    onPrimaryContainer = Color(0xFF9CF4FF),
    secondary = BlueLaser,
    onSecondary = Color.White,
    tertiary = EmeraldTelemetry,
    background = TechNavy900,
    surface = TechNavy800,
    surfaceVariant = TechNavy700,
    outline = TechNavy600
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep cohesive tech aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
