package com.example.agendaencarta2004v3.actividades.ui

// Compose core / runtime
import android.view.ContextThemeWrapper
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.agendaencarta2004v3.R
// Compose foundation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

// Compose Material3
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState

// Material Icons (M3)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow


import com.example.agendaencarta2004v3.actividades.viewmodel.ActividadViewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel

// Kotlin / Java
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.ZoneId



// -------------------- helpers --------------------

enum class FiltroEstado { TODAS, HECHAS, POR_HACER, VENCIDAS }

/** Medianoche de hoy en millis */
private fun hoyMillis(): Long {
    val cal = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

@Composable
private fun EstadoFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, maxLines = 1) },
        leadingIcon = if (selected) { { Icon(Icons.Outlined.Check, null) } } else null
    )
}

// -------------------- UI principal --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesScreen(
    actividadViewModel: ActividadViewModel,
    bibliotecaViewModel: BibliotecaViewModel
) {
    val actividades by actividadViewModel.actividades.collectAsState()
    val cursos by bibliotecaViewModel.cursos.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var filtro by remember { mutableStateOf(FiltroEstado.TODAS) }
    val dateFormat = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) }

    val actividadesFiltradas = remember(actividades, filtro) {
        when (filtro) {
            FiltroEstado.TODAS     -> actividades
            FiltroEstado.HECHAS    -> actividades.filter { it.hecho }
            FiltroEstado.POR_HACER -> actividades.filter { !it.hecho && it.fechaEntrega >= hoyMillis() }
            FiltroEstado.VENCIDAS  -> actividades.filter { !it.hecho && it.fechaEntrega <  hoyMillis() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actividades") } // ⬅️ sin acciones; solo el título
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Añadir")
            }
        }
    ) { inner ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize().padding(inner)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ---- Filtros en una sola línea (scroll) ----
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        EstadoFilterChip("Todas", filtro == FiltroEstado.TODAS)     { filtro = FiltroEstado.TODAS }
                    }
                    item {
                        EstadoFilterChip("Por hacer", filtro == FiltroEstado.POR_HACER) { filtro = FiltroEstado.POR_HACER }
                    }
                    item {
                        EstadoFilterChip("Hechas", filtro == FiltroEstado.HECHAS)     { filtro = FiltroEstado.HECHAS }
                    }
                    item {
                        EstadoFilterChip("Vencidas", filtro == FiltroEstado.VENCIDAS)  { filtro = FiltroEstado.VENCIDAS }
                    }
                }

                // ---- Lista con swipe (solo borrar) ----
                if (actividadesFiltradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay actividades para mostrar.")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(
                            items = actividadesFiltradas,
                            key = { it.id }
                        ) { act ->
                            val cursoNombre = cursos.firstOrNull { it.id == act.cursoId }?.nombre ?: "Curso"
                            val vencida = !act.hecho && act.fechaEntrega < hoyMillis()

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { target ->
                                    when (target) {
                                        // bloqueamos StartToEnd (marcar hecha via swipe)
                                        SwipeToDismissBoxValue.StartToEnd -> false
                                        // solo permitimos borrar con EndToStart
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            actividadViewModel.deleteActividad(act)
                                            true
                                        }
                                        else -> false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val target = dismissState.targetValue
                                    val bg = when (target) {
                                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                    val icon = when (target) {
                                        SwipeToDismissBoxValue.EndToStart -> Icons.Outlined.Delete
                                        else -> null
                                    }
                                    Box(
                                        Modifier.fillMaxSize().background(bg).padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) { if (icon != null) Icon(icon, null) }
                                },
                                content = {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    when {
                                                        act.hecho -> {
                                                            Icon(
                                                                Icons.Outlined.CheckCircle,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.tertiary
                                                            )
                                                            Spacer(Modifier.width(6.dp))
                                                        }
                                                        vencida -> {
                                                            Icon(
                                                                Icons.Outlined.Warning,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.error
                                                            )
                                                            Spacer(Modifier.width(6.dp))
                                                        }
                                                    }
                                                    Text(
                                                        act.descripcion,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    "Curso: $cursoNombre",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    "Entrega: ${dateFormat.format(java.util.Date(act.fechaEntrega))}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (vencida) MaterialTheme.colorScheme.error
                                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Checkbox(
                                                checked = act.hecho,
                                                onCheckedChange = { checked ->
                                                    actividadViewModel.toggleActividad(act, checked)
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ---- Formulario (diálogo) ----
    if (showForm) {
        CrearActividadDialog(
            cursos = cursos,
            onDismiss = { showForm = false },
            onSave = { desc, cursoId, fechaMillis ->
                actividadViewModel.addActividad(desc, cursoId, fechaMillis)
                showForm = false
            }
        )
    }
}

// -------------------- diálogo crear actividad --------------------

@Composable
private fun FechaField(
    label: String,
    valueMillis: Long?,
    onDatePicked: (Long) -> Unit
) {
    val context = LocalContext.current
    val sdf = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) }
    val shown = valueMillis?.let { sdf.format(java.util.Date(it)) } ?: "Seleccionar fecha"
    OutlinedButton(
        onClick = {
            // Abrir DatePicker nativo (ventana emergente)
            val cal = java.util.Calendar.getInstance().apply {
                timeInMillis = valueMillis ?: System.currentTimeMillis()
            }

            // 🔹 aplicar tu tema oscuro aquí
            val themedContext = ContextThemeWrapper(context, R.style.MyDatePickerDialogTheme)

            val dialog = android.app.DatePickerDialog(
                themedContext,
                { _, y, m, d ->
                    val picked = java.util.Calendar.getInstance().apply {
                        set(y, m, d, 0, 0, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    onDatePicked(picked)
                },
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Outlined.Today, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Seleccionar fecha")
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrearActividadDialog(
    cursos: List<CursoEntity>,
    onDismiss: () -> Unit,
    onSave: (descripcion: String, cursoId: Int, fechaEntregaMillis: Long) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var selectedCurso by remember { mutableStateOf<CursoEntity?>(null) }
    var fechaMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var cursoExpanded by remember { mutableStateOf(false) }

    val canSave = remember(desc, selectedCurso, fechaMillis) {
        desc.isNotBlank() && selectedCurso != null && fechaMillis != null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    onSave(desc.trim(), selectedCurso!!.id, fechaMillis!!)
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Nueva actividad") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Curso (ExposedDropdown)
                ExposedDropdownMenuBox(
                    expanded = cursoExpanded,
                    onExpandedChange = { cursoExpanded = !cursoExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCurso?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cursoExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = cursoExpanded,
                        onDismissRequest = { cursoExpanded = false }
                    ) {
                        cursos.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.nombre) },
                                onClick = {
                                    selectedCurso = c
                                    cursoExpanded = false
                                }
                            )
                        }
                    }
                }

                // Fecha (abre un DatePicker nativo en ventana aparte)
                FechaField(
                    label = "Fecha de entrega",
                    valueMillis = fechaMillis,
                    onDatePicked = { picked -> fechaMillis = picked }
                )
            }
        }
    )
}

