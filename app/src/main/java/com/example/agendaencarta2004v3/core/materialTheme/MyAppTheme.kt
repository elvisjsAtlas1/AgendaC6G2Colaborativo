package com.example.agendaencarta2004v3.core.materialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 🎨 Paleta personalizada
private val MyColorScheme = lightColorScheme(
    primary = Color(0xFF04ADBF),   // Principal → Botones, resaltados
    secondary = Color(0xFF03658C), // Secundario → Detalles, iconos
    tertiary = Color(0xFF0388A6),  // Apoyo → Acciones especiales
    background = Color(0xFF023059), // Fondo de la app
    surface = Color(0xFF023E73),   // Tarjetas, menús, superficies
    onPrimary = Color.White,       // Texto sobre primary
    onSecondary = Color.White,     // Texto sobre secondary
    onTertiary = Color.White,      // Texto sobre tertiary
    onBackground = Color.White,    // Texto sobre background
    onSurface = Color.White        // Texto sobre surface
)

// 🖌️ Tema global
@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MyColorScheme,
        typography = Typography(), // Puedes personalizar después
        content = content
    )

}
@Composable
fun DropdownMenuItemStyled(
    text: String,
    selected: Boolean = false,  // ✅ Agregado para resaltar seleccionado
    onClick: () -> Unit
) {
    DropdownMenuItem(
        onClick = onClick,
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (selected) Color(0xFF04ADBF) // Azul más claro si está seleccionado
                        else Color(0xFF03658C)        // Fondo oscuro para los demás
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = text,
                    color = Color.White // Texto blanco
                )
            }
        }
    )
}

