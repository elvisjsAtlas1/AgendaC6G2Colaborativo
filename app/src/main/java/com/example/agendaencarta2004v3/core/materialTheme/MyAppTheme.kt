package com.example.agendaencarta2004v3.core.materialTheme
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Paleta fallback (sobria) – tonos teal/indigo/gris
private val LightColors = lightColorScheme(
    primary = Color(0xFF0EA5A6),          // Teal sobrio
    onPrimary = Color.White,
    secondary = Color(0xFF6366F1),        // Indigo soft
    onSecondary = Color.White,
    tertiary = Color(0xFF10B981),         // Emerald
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),       // Slate-50
    onBackground = Color(0xFF0F172A),     // Slate-900
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF475569),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF00323A),
    secondary = Color(0xFF8EA2FF),
    onSecondary = Color(0xFF0D1333),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF003222),
    background = Color(0xFF0B1220),       // Azul gris muy oscuro
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF0F172A),          // “Card”/Sheet
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFCBD5E1),
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic color (Android 12+) con fallback sobrio
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamic = if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
            dynamic
        }
        else -> if (darkTheme) DarkColors else LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),   // luego puedes cargar Inter/RobotoFlex
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8),
            small      = RoundedCornerShape(12),
            medium     = RoundedCornerShape(16),
            large      = RoundedCornerShape(20),
            extraLarge = RoundedCornerShape(28)
        ),
        content = content
    )
}
