package com.example.agendaencarta2004v3.OnBoarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agendaencarta2004v3.core.navigation.Screen
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.launch


@Composable
fun OnboardingScreen(
    navController: NavController,
    onFinish: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pages = remember {
        listOf(
            OnboardPage(
                title = "Planifica tu semana",
                desc = "Crea tu horario por día y hora. Toca un bloque para ver, editar o eliminar.",
                icon = Icons.Outlined.CalendarMonth,
                bullets = listOf(
                    "Timeline con scroll vertical y horizontal",
                    "Eventos por curso, hora y aula",
                    "Edición rápida desde la tarjeta"
                )
            ),
            OnboardPage(
                title = "Gestiona actividades",
                desc = "Registra tareas con fecha. Desliza para marcar como hecha o eliminar.",
                icon = Icons.Outlined.TaskAlt,
                bullets = listOf(
                    "Filtros: Todas, Por hacer, Hechas, Vencidas",
                    "Swipe (Material 3) para acciones rápidas",
                    "Checkbox para completar"
                )
            ),
            OnboardPage(
                title = "Tu biblioteca de cursos",
                desc = "Organiza materiales por curso y semana. Adjunta documentos, imágenes o enlaces.",
                icon = Icons.Outlined.LibraryBooks,
                bullets = listOf(
                    "Semanas por curso",
                    "Material: documento, imagen o link",
                    "Abrir archivos con un toque"
                )
            ),
            OnboardPage(
                title = "Resumen con gráficas",
                desc = "Mira tu progreso semanal con un gráfico de barras táctil.",
                icon = Icons.Outlined.BarChart,
                bullets = listOf(
                    "Semanas dinámicas según datos",
                    "Etiquetas legibles por fecha",
                    "Tap para ver detalle por semana"
                )
            ),
            OnboardPage(
                title = "Añade por voz",
                desc = "Dí: “agrega tarea de mate para mañana”. La app detecta curso y fecha.",
                icon = Icons.Outlined.Mic,
                bullets = listOf(
                    "Fechas relativas: hoy, mañana, pasado",
                    "Soporta dd/MM y “21 de setiembre”",
                    "Curso por coincidencia inteligente"
                )
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })

    // Fondo oscuro con degradado sutil
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B1220), // fondo profundo
                        Color(0xFF0F172A)  // surface dark
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(12.dp))

            // Pager (contenido)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardPageCard(page = pages[page])
            }

            // Indicadores
            DotsIndicator(
                total = pages.size,
                selectedIndex = pagerState.currentPage
            )

            // Botonera inferior
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (pagerState.currentPage > 0) {
                    OutlinedButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) { Text("Atrás") }
                }

                val isLast = pagerState.currentPage == pages.lastIndex
                FilledTonalButton(
                    onClick = {
                        if (isLast) {
                            onFinish()
                            navController.navigate(Screen.Inicio.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                ) {
                    if (isLast) Text("Comenzar")
                    else {
                        Text("Siguiente")
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }

        }
    }
}

@Composable
private fun OnboardPageCard(page: OnboardPage) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icono grande
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = page.desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Bullets
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                page.bullets.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DotsIndicator(total: Int, selectedIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(total) { index ->
            val color by animateColorAsState(
                if (index == selectedIndex) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                label = "dotColor"
            )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (index == selectedIndex) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

private data class OnboardPage(
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val bullets: List<String>
)


