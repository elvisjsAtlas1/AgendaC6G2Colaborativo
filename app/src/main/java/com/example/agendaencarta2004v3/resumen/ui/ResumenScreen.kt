package com.example.agendaencarta2004v3.resumen.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.agendaencarta2004v3.resumen.viewmodel.ResumenViewModel
import com.example.agendaencarta2004v3.resumen.viewmodel.WeekStat
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    resumenViewModel: ResumenViewModel
) {
    val stats by resumenViewModel.stats.collectAsState()

    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Resumen") }) }
    ) { inner ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Actividades realizadas por semana", style = MaterialTheme.typography.titleMedium)

                if (stats.isEmpty()) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Text(
                            "Aún no hay actividades realizadas en el rango.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    ElevatedCard {
                        ChartRow(stats)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartRow(stats: List<WeekStat>) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scroll)
            .padding(16.dp)
    ) {
        BarChartSemanal(
            data = stats,
            barWidth = 36.dp,
            barGap = 18.dp,
            maxChartHeight = 240.dp
        )
    }
}


@Composable
fun BarChartSemanal(
    data: List<WeekStat>,
    barWidth: Dp,
    barGap: Dp,
    maxChartHeight: Dp
) {
    // --- Escala “bonita” para eje Y ---
    val maxValueRaw = (data.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    val (maxValue, step) = remember(maxValueRaw) { niceScale(maxValueRaw) }

    // --- Estado del tooltip ---
    var seleccionado by remember { mutableStateOf<WeekStat?>(null) }

    // --- Márgenes / densidad ---
    val leftInset = 40.dp
    val bottomInset = 16.dp
    val density = LocalDensity.current

    // ✅ Captura de colores de MaterialTheme fuera de Canvas/pointerInput
    val cs = MaterialTheme.colorScheme
    val colorGuide      = cs.surfaceVariant
    val colorGuideText  = cs.onSurfaceVariant
    val colorBar        = cs.primary
    val colorBarText    = cs.onPrimary

    Column {
        // Área del gráfico
        Box(
            modifier = Modifier
                .height(maxChartHeight)
                .width((data.size * (barWidth + barGap).value).dp + leftInset)
                .padding(bottom = bottomInset)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val barW = with(density) { barWidth.toPx() }
                val gap = with(density) { barGap.toPx() }
                val leftPx = with(density) { leftInset.toPx() }

                // ---- Líneas guía + labels eje Y ----
                for (yVal in 0..maxValue step step) {
                    val yRatio = yVal.toFloat() / maxValue.toFloat()
                    val y = h - (h - 8f) * yRatio

                    // guía horizontal
                    drawLine(
                        color = colorGuide,
                        start = Offset(leftPx, y),
                        end = Offset(w, y),
                        strokeWidth = if (yVal == 0) 2f else 1f
                    )

                    // label a la izquierda
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = colorGuideText.toArgb()
                            textAlign = android.graphics.Paint.Align.RIGHT
                            textSize = 24f
                            isAntiAlias = true
                        }
                        drawText(yVal.toString(), leftPx - 8f, y - 4f, paint)
                    }
                }

                // ---- Barras ----
                data.forEachIndexed { i, stat ->
                    val x = leftPx + i * (barW + gap)
                    val ratio = stat.count.toFloat() / maxValue.toFloat()
                    val barH = (h - 12f) * ratio
                    val top = h - barH

                    drawRoundRect(
                        color = colorBar,
                        topLeft = Offset(x, top),
                        size = androidx.compose.ui.geometry.Size(barW, barH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                    )

                    if (barH > 22f) {
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = colorBarText.toArgb()
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 28f
                                isAntiAlias = true
                            }
                            drawText(stat.count.toString(), x + barW / 2f, top - 6f, paint)
                        }
                    }
                }
            }

            // Overlay para taps → tooltip
            Box(
                Modifier
                    .matchParentSize()
                    .pointerInput(data, barWidth, barGap, leftInset) {
                        detectTapGestures { offset ->
                            val barW = with(density) { barWidth.toPx() }
                            val gap = with(density) { barGap.toPx() }
                            val leftPx = with(density) { leftInset.toPx() }
                            val i = ((offset.x - leftPx) / (barW + gap)).toInt()
                            if (i in data.indices) seleccionado = data[i]
                        }
                    }
            )
        }

        // Etiquetas bajo las barras (día–día y mes-año)
        Row(
            modifier = Modifier
                .padding(start = leftInset)
                .width((data.size * (barWidth + barGap).value).dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(barGap)
        ) {
            data.forEach { stat ->
                val start = stat.start
                val end = stat.end
                val monthLabel = end.month.getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    Locale.getDefault()
                )
                val line1 = String.format("%02d–%02d", start.dayOfMonth, end.dayOfMonth)
                val line2 = "$monthLabel ${end.year}"

                Column(
                    modifier = Modifier.width(barWidth),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(line1, style = MaterialTheme.typography.labelSmall, color = cs.onSurface)
                    Text(line2, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                }
            }
        }
    }

    // Tooltip simple
    if (seleccionado != null) {
        val s = seleccionado!!
        val fmt = java.time.format.DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.getDefault())
        val rango = "${s.start.format(fmt)} — ${s.end.format(fmt)}"
        AlertDialog(
            onDismissRequest = { seleccionado = null },
            confirmButton = { TextButton(onClick = { seleccionado = null }) { Text("OK") } },
            title = { Text("Semana") },
            text = {
                Column {
                    Text(rango)
                    Spacer(Modifier.height(8.dp))
                    Text("Actividades realizadas: ${s.count}")
                }
            }
        )
    }
}

/** Escala “bonita” para el eje Y (máximo y paso). No composable. */
private fun niceScale(maxVal: Int): Pair<Int, Int> {
    if (maxVal <= 5) return 5 to 1
    val bases = intArrayOf(1, 2, 5)
    var magnitude = 1
    while (true) {
        for (b in bases) {
            val step = b * magnitude
            val maxY = ((maxVal + step - 1) / step) * step
            val ticks = maxY / step
            if (ticks in 4..8) return maxY to step
        }
        magnitude *= 10
        if (magnitude > 1_000_000) return maxVal to 1
    }
}