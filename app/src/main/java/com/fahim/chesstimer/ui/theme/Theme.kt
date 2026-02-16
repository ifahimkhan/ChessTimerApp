package com.fahim.chesstimer.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ==========================================
// Theme Style Enum
// ==========================================

enum class ThemeStyle { GRANDMASTER, BLITZ, FOREST_CLASSICAL }

// ==========================================
// Extended Colors for Chess-Specific UI
// ==========================================

@Immutable
data class ChessTimerExtendedColors(
    val whitePlayerBackground: Color,
    val whitePlayerText: Color,
    val blackPlayerBackground: Color,
    val blackPlayerText: Color,
    val activeAccent: Color,
    val lowTimeWarning: Color,
    val controlBarBackground: Color,
    val controlBarContent: Color
)

val LocalChessTimerColors = staticCompositionLocalOf {
    ChessTimerExtendedColors(
        whitePlayerBackground = Color.White,
        whitePlayerText = Color.Black,
        blackPlayerBackground = Color.Black,
        blackPlayerText = Color.White,
        activeAccent = Color.Green,
        lowTimeWarning = Color.Red,
        controlBarBackground = Color.DarkGray,
        controlBarContent = Color.White
    )
}

// ==========================================
// Grandmaster Color Schemes
// ==========================================

private val GrandmasterLightScheme = lightColorScheme(
    primary = GmPrimaryLight,
    onPrimary = GmOnPrimaryLight,
    primaryContainer = GmPrimaryContainerLight,
    onPrimaryContainer = GmOnPrimaryContainerLight,
    secondary = GmSecondaryLight,
    onSecondary = GmOnSecondaryLight,
    secondaryContainer = GmSecondaryContainerLight,
    onSecondaryContainer = GmOnSecondaryContainerLight,
    tertiary = GmTertiaryLight,
    onTertiary = GmOnTertiaryLight,
    tertiaryContainer = GmTertiaryContainerLight,
    onTertiaryContainer = GmOnTertiaryContainerLight,
    background = GmBackgroundLight,
    onBackground = GmOnBackgroundLight,
    surface = GmSurfaceLight,
    onSurface = GmOnSurfaceLight,
    surfaceVariant = GmSurfaceVariantLight,
    onSurfaceVariant = GmOnSurfaceVariantLight,
    outline = GmOutlineLight,
    outlineVariant = GmOutlineVariantLight,
    error = GmErrorLight,
    onError = GmOnErrorLight
)

private val GrandmasterDarkScheme = darkColorScheme(
    primary = GmPrimaryDark,
    onPrimary = GmOnPrimaryDark,
    primaryContainer = GmPrimaryContainerDark,
    onPrimaryContainer = GmOnPrimaryContainerDark,
    secondary = GmSecondaryDark,
    onSecondary = GmOnSecondaryDark,
    secondaryContainer = GmSecondaryContainerDark,
    onSecondaryContainer = GmOnSecondaryContainerDark,
    tertiary = GmTertiaryDark,
    onTertiary = GmOnTertiaryDark,
    tertiaryContainer = GmTertiaryContainerDark,
    onTertiaryContainer = GmOnTertiaryContainerDark,
    background = GmBackgroundDark,
    onBackground = GmOnBackgroundDark,
    surface = GmSurfaceDark,
    onSurface = GmOnSurfaceDark,
    surfaceVariant = GmSurfaceVariantDark,
    onSurfaceVariant = GmOnSurfaceVariantDark,
    outline = GmOutlineDark,
    outlineVariant = GmOutlineVariantDark,
    error = GmErrorDark,
    onError = GmOnErrorDark
)

private val GrandmasterExtendedLight = ChessTimerExtendedColors(
    whitePlayerBackground = GmWhitePlayerLight,
    whitePlayerText = GmWhitePlayerTextLight,
    blackPlayerBackground = GmBlackPlayerLight,
    blackPlayerText = GmBlackPlayerTextLight,
    activeAccent = GmPrimaryLight,
    lowTimeWarning = LowTimeWarning,
    controlBarBackground = GmControlBarLight,
    controlBarContent = GmControlBarContentLight
)

private val GrandmasterExtendedDark = ChessTimerExtendedColors(
    whitePlayerBackground = GmWhitePlayerDark,
    whitePlayerText = GmWhitePlayerTextDark,
    blackPlayerBackground = GmBlackPlayerDark,
    blackPlayerText = GmBlackPlayerTextDark,
    activeAccent = GmActiveAccent,
    lowTimeWarning = LowTimeWarning,
    controlBarBackground = GmControlBarDark,
    controlBarContent = GmControlBarContentDark
)

// ==========================================
// Blitz Color Schemes
// ==========================================

private val BlitzLightScheme = lightColorScheme(
    primary = BzPrimaryLight,
    onPrimary = BzOnPrimaryLight,
    primaryContainer = BzPrimaryContainerLight,
    onPrimaryContainer = BzOnPrimaryContainerLight,
    secondary = BzSecondaryLight,
    onSecondary = BzOnSecondaryLight,
    secondaryContainer = BzSecondaryContainerLight,
    onSecondaryContainer = BzOnSecondaryContainerLight,
    tertiary = BzTertiaryLight,
    onTertiary = BzOnTertiaryLight,
    tertiaryContainer = BzTertiaryContainerLight,
    onTertiaryContainer = BzOnTertiaryContainerLight,
    background = BzBackgroundLight,
    onBackground = BzOnBackgroundLight,
    surface = BzSurfaceLight,
    onSurface = BzOnSurfaceLight,
    surfaceVariant = BzSurfaceVariantLight,
    onSurfaceVariant = BzOnSurfaceVariantLight,
    outline = BzOutlineLight,
    outlineVariant = BzOutlineVariantLight,
    error = BzErrorLight,
    onError = BzOnErrorLight
)

private val BlitzDarkScheme = darkColorScheme(
    primary = BzPrimaryDark,
    onPrimary = BzOnPrimaryDark,
    primaryContainer = BzPrimaryContainerDark,
    onPrimaryContainer = BzOnPrimaryContainerDark,
    secondary = BzSecondaryDark,
    onSecondary = BzOnSecondaryDark,
    secondaryContainer = BzSecondaryContainerDark,
    onSecondaryContainer = BzOnSecondaryContainerDark,
    tertiary = BzTertiaryDark,
    onTertiary = BzOnTertiaryDark,
    tertiaryContainer = BzTertiaryContainerDark,
    onTertiaryContainer = BzOnTertiaryContainerDark,
    background = BzBackgroundDark,
    onBackground = BzOnBackgroundDark,
    surface = BzSurfaceDark,
    onSurface = BzOnSurfaceDark,
    surfaceVariant = BzSurfaceVariantDark,
    onSurfaceVariant = BzOnSurfaceVariantDark,
    outline = BzOutlineDark,
    outlineVariant = BzOutlineVariantDark,
    error = BzErrorDark,
    onError = BzOnErrorDark
)

private val BlitzExtendedLight = ChessTimerExtendedColors(
    whitePlayerBackground = BzWhitePlayerLight,
    whitePlayerText = BzWhitePlayerTextLight,
    blackPlayerBackground = BzBlackPlayerLight,
    blackPlayerText = BzBlackPlayerTextLight,
    activeAccent = BzPrimaryLight,
    lowTimeWarning = LowTimeWarning,
    controlBarBackground = BzControlBarLight,
    controlBarContent = BzControlBarContentLight
)

private val BlitzExtendedDark = ChessTimerExtendedColors(
    whitePlayerBackground = BzWhitePlayerDark,
    whitePlayerText = BzWhitePlayerTextDark,
    blackPlayerBackground = BzBlackPlayerDark,
    blackPlayerText = BzBlackPlayerTextDark,
    activeAccent = BzActiveAccent,
    lowTimeWarning = LowTimeWarning,
    controlBarBackground = BzControlBarDark,
    controlBarContent = BzControlBarContentDark
)

// ==========================================
// Forest Classical Color Schemes
// ==========================================

private val ForestClassicalLightScheme = lightColorScheme(
    primary = FcPrimaryLight,
    onPrimary = FcOnPrimaryLight,
    primaryContainer = FcPrimaryContainerLight,
    onPrimaryContainer = FcOnPrimaryContainerLight,
    secondary = FcSecondaryLight,
    onSecondary = FcOnSecondaryLight,
    secondaryContainer = FcSecondaryContainerLight,
    onSecondaryContainer = FcOnSecondaryContainerLight,
    tertiary = FcTertiaryLight,
    onTertiary = FcOnTertiaryLight,
    tertiaryContainer = FcTertiaryContainerLight,
    onTertiaryContainer = FcOnTertiaryContainerLight,
    background = FcBackgroundLight,
    onBackground = FcOnBackgroundLight,
    surface = FcSurfaceLight,
    onSurface = FcOnSurfaceLight,
    surfaceVariant = FcSurfaceVariantLight,
    onSurfaceVariant = FcOnSurfaceVariantLight,
    outline = FcOutlineLight,
    outlineVariant = FcOutlineVariantLight,
    error = FcErrorLight,
    onError = FcOnErrorLight
)

private val ForestClassicalDarkScheme = darkColorScheme(
    primary = FcPrimaryDark,
    onPrimary = FcOnPrimaryDark,
    primaryContainer = FcPrimaryContainerDark,
    onPrimaryContainer = FcOnPrimaryContainerDark,
    secondary = FcSecondaryDark,
    onSecondary = FcOnSecondaryDark,
    secondaryContainer = FcSecondaryContainerDark,
    onSecondaryContainer = FcOnSecondaryContainerDark,
    tertiary = FcTertiaryDark,
    onTertiary = FcOnTertiaryDark,
    tertiaryContainer = FcTertiaryContainerDark,
    onTertiaryContainer = FcOnTertiaryContainerDark,
    background = FcBackgroundDark,
    onBackground = FcOnBackgroundDark,
    surface = FcSurfaceDark,
    onSurface = FcOnSurfaceDark,
    surfaceVariant = FcSurfaceVariantDark,
    onSurfaceVariant = FcOnSurfaceVariantDark,
    outline = FcOutlineDark,
    outlineVariant = FcOutlineVariantDark,
    error = FcErrorDark,
    onError = FcOnErrorDark
)

private val ForestClassicalExtendedLight = ChessTimerExtendedColors(
    whitePlayerBackground = FcWhitePlayerLight,
    whitePlayerText = FcWhitePlayerTextLight,
    blackPlayerBackground = FcBlackPlayerLight,
    blackPlayerText = FcBlackPlayerTextLight,
    activeAccent = FcPrimaryLight,
    lowTimeWarning = LowTimeWarning,
    controlBarBackground = FcControlBarLight,
    controlBarContent = FcControlBarContentLight
)

private val ForestClassicalExtendedDark = ChessTimerExtendedColors(
    whitePlayerBackground = FcWhitePlayerDark,
    whitePlayerText = FcWhitePlayerTextDark,
    blackPlayerBackground = FcBlackPlayerDark,
    blackPlayerText = FcBlackPlayerTextDark,
    activeAccent = FcActiveAccent,
    lowTimeWarning = LowTimeWarning,
    controlBarBackground = FcControlBarDark,
    controlBarContent = FcControlBarContentDark
)

// ==========================================
// Shape Systems
// ==========================================

private val GrandmasterShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val BlitzShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp)
)

private val ForestClassicalShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// ==========================================
// Theme Composable
// ==========================================

@Composable
fun ChessTimerTheme(
    themeStyle: ThemeStyle = ThemeStyle.GRANDMASTER,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme
    val extendedColors: ChessTimerExtendedColors
    val shapes: Shapes

    when (themeStyle) {
        ThemeStyle.GRANDMASTER -> {
            colorScheme = if (darkTheme) GrandmasterDarkScheme else GrandmasterLightScheme
            extendedColors = if (darkTheme) GrandmasterExtendedDark else GrandmasterExtendedLight
            shapes = GrandmasterShapes
        }
        ThemeStyle.BLITZ -> {
            colorScheme = if (darkTheme) BlitzDarkScheme else BlitzLightScheme
            extendedColors = if (darkTheme) BlitzExtendedDark else BlitzExtendedLight
            shapes = BlitzShapes
        }
        ThemeStyle.FOREST_CLASSICAL -> {
            colorScheme = if (darkTheme) ForestClassicalDarkScheme else ForestClassicalLightScheme
            extendedColors = if (darkTheme) ForestClassicalExtendedDark else ForestClassicalExtendedLight
            shapes = ForestClassicalShapes
        }
    }

    // Update status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = extendedColors.controlBarBackground.toArgb()
            window.navigationBarColor = extendedColors.controlBarBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalChessTimerColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ChessTimerTypography,
            shapes = shapes,
            content = content
        )
    }
}
