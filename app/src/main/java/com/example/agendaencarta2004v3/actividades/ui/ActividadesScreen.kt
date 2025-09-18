package com.example.agendaencarta2004v3.actividades.ui

// Compose core / runtime
import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.text.KeyboardOptions

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
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.NotificationManagerCompat
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import com.example.agendaencarta2004v3.actividades.reminder.ReminderReceiver
import com.example.agendaencarta2004v3.actividades.reminder.ReminderScheduler


import com.example.agendaencarta2004v3.actividades.viewmodel.ActividadViewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel

// Kotlin / Java
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar


// -------------------- helpers --------------------
// Mantén tu enum
enum class FiltroEstado { TODAS, HECHAS, POR_HACER, VENCIDAS }

/** Medianoche de hoy en millis (lo dejamos por si lo usas en otros sitios) */
private fun hoyMillis(): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
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

/* ------------------ Helpers recordatorio ------------------ */

@RequiresApi(Build.VERSION_CODES.O)
private fun isTomorrow(dateMillisAt00: Long): Boolean {
    val now = Calendar.getInstance()
    val tgt = Calendar.getInstance().apply { timeInMillis = dateMillisAt00 }
    // Normaliza ambas a 00:00
    now.set(Calendar.HOUR_OF_DAY, 0); now.set(Calendar.MINUTE, 0)
    now.set(Calendar.SECOND, 0); now.set(Calendar.MILLISECOND, 0)
    tgt.set(Calendar.HOUR_OF_DAY, 0); tgt.set(Calendar.MINUTE, 0)
    tgt.set(Calendar.SECOND, 0); tgt.set(Calendar.MILLISECOND, 0)

    now.add(Calendar.DAY_OF_YEAR, 1)
    return now.get(Calendar.YEAR) == tgt.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == tgt.get(Calendar.DAY_OF_YEAR)
}

/** Devuelve pares (textoVisible, minutosAntes) */
@RequiresApi(Build.VERSION_CODES.O)
private fun reminderOptions(dateMillisAt00: Long): List<Pair<String, Int>> {
    return if (isTomorrow(dateMillisAt00)) {
        listOf(
            "3 horas antes" to 3 * 60,
            "5 horas antes" to 5 * 60,
            "12 horas antes" to 12 * 60
        )
    } else {
        listOf(
            "1 día antes" to 24 * 60,
            "2 días antes" to 2 * 24 * 60,
            "3 días antes" to 3 * 24 * 60,
            "7 días antes" to 7 * 24 * 60
        )
    }
}

/** Une fecha (00:00) + hora(min) ⇒ timestamp final */
private fun combineDateAndTime(dateAt00: Long, hour: Int, minute: Int): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = dateAt00
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

/* ------------------ Permiso notificaciones ------------------ */

@Composable
private fun rememberNotifPermissionLauncher(): androidx.activity.result.ActivityResultLauncher<String> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Toast.makeText(context, "Activa las notificaciones para recibir avisos", Toast.LENGTH_LONG).show()
        }
    }
}

/* ------------------ Campo Fecha ------------------ */


@Composable
private fun FechaField(
    label: String,
    valueMillis: Long?,
    onDatePicked: (Long) -> Unit
) {
    val context = LocalContext.current
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val shown = valueMillis?.let { sdf.format(Date(it)) } ?: "Seleccionar fecha"

    OutlinedButton(
        onClick = {
            val cal = Calendar.getInstance().apply {
                timeInMillis = valueMillis ?: System.currentTimeMillis()
            }
            val themedContext = ContextThemeWrapper(context, R.style.MyDatePickerDialogTheme)
            DatePickerDialog(
                themedContext,
                { _, y, m, d ->
                    val picked = Calendar.getInstance().apply {
                        set(y, m, d, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    onDatePicked(picked)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Outlined.Today, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(shown)
    }
}

/* ------------------ Campo Hora ------------------ */

@Composable
private fun HoraField(
    label: String,
    enabled: Boolean,
    hour: Int?,
    minute: Int?,
    onTimePicked: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val display = if (hour != null && minute != null) {
        "%02d:%02d".format(hour, minute)
    } else "Seleccionar hora"

    OutlinedButton(
        onClick = {
            val now = java.util.Calendar.getInstance()
            val h0 = hour ?: now.get(java.util.Calendar.HOUR_OF_DAY)
            val m0 = minute ?: now.get(java.util.Calendar.MINUTE)

            // 👇 Envolvemos con el tema oscuro
            val themedContext = android.view.ContextThemeWrapper(
                context,
                R.style.MyTimePickerDialogTheme
            )

            android.app.TimePickerDialog(
                themedContext,
                { _, h, m -> onTimePicked(h, m) },
                h0, m0, true
            ).show()
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Outlined.Schedule, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(display)
    }
}


/* ------------------ UI principal ------------------ */

@RequiresApi(Build.VERSION_CODES.O)
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

    // Formato fecha+hora para visualizar
    val dateTimeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // Filtrado usando la HORA actual (no medianoche)
    val now = System.currentTimeMillis()
    val actividadesFiltradas = remember(actividades, filtro, now) {
        when (filtro) {
            FiltroEstado.TODAS     -> actividades
            FiltroEstado.HECHAS    -> actividades.filter { it.hecho }
            FiltroEstado.POR_HACER -> actividades.filter { !it.hecho && it.fechaEntrega >= now }
            FiltroEstado.VENCIDAS  -> actividades.filter { !it.hecho && it.fechaEntrega <  now }
        }
    }

    val context = LocalContext.current
    val notifLauncher = rememberNotifPermissionLauncher()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Actividades") }) },
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
                // Filtros
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { EstadoFilterChip("Todas",   filtro == FiltroEstado.TODAS)   { filtro = FiltroEstado.TODAS } }
                    item { EstadoFilterChip("Por hacer", filtro == FiltroEstado.POR_HACER) { filtro = FiltroEstado.POR_HACER } }
                    item { EstadoFilterChip("Hechas",  filtro == FiltroEstado.HECHAS)  { filtro = FiltroEstado.HECHAS } }
                    item { EstadoFilterChip("Vencidas",filtro == FiltroEstado.VENCIDAS){ filtro = FiltroEstado.VENCIDAS } }
                }

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
                            val vencida = !act.hecho && act.fechaEntrega < System.currentTimeMillis()

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { target ->
                                    when (target) {
                                        SwipeToDismissBoxValue.StartToEnd -> false // bloquear marcar hecha por swipe
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
                                                            Icon(Icons.Outlined.CheckCircle, null,
                                                                tint = MaterialTheme.colorScheme.tertiary)
                                                            Spacer(Modifier.width(6.dp))
                                                        }
                                                        vencida -> {
                                                            Icon(Icons.Outlined.Warning, null,
                                                                tint = MaterialTheme.colorScheme.error)
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
                                                    "Entrega: ${dateTimeFormat.format(Date(act.fechaEntrega))}",
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

    // ---- Formulario (diálogo) FECHA+HORA + RECORDATORIO ----
    if (showForm) {
        CrearActividadDialog(
            cursos = cursos,
            onDismiss = { showForm = false },
            onSave = { desc, cursoId, fechaHoraEntregaMillis, avisoMinAntes ->
                // Permiso Android 13+ si se quiere notificar
                if (avisoMinAntes != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                actividadViewModel.addActividad(desc, cursoId, fechaHoraEntregaMillis, avisoMinAntes)
                showForm = false
            }
        )
    }
}

/* ------------------ Diálogo crear actividad ------------------ */

private enum class ReminderUnit { MINUTOS, HORAS, DIAS }

private fun ReminderUnit.label(): String = when (this) {
    ReminderUnit.MINUTOS -> "Minutos"
    ReminderUnit.HORAS   -> "Horas"
    ReminderUnit.DIAS    -> "Días"
}

private fun minutesFrom(count: Int, unit: ReminderUnit): Int = when (unit) {
    ReminderUnit.MINUTOS -> count
    ReminderUnit.HORAS   -> count * 60
    ReminderUnit.DIAS    -> count * 24 * 60
}

/** 2880 -> "2 días", 120 -> "2 horas", 15 -> "15 min" */
private fun humanizeMinutes(mins: Int): String = when {
    mins % (24 * 60) == 0 -> {
        val d = mins / (24 * 60); if (d == 1) "1 día" else "$d días"
    }
    mins % 60 == 0 -> {
        val h = mins / 60; if (h == 1) "1 hora" else "$h horas"
    }
    else -> "$mins min"
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrearActividadDialog(
    cursos: List<CursoEntity>,
    onDismiss: () -> Unit,
    onSave: (descripcion: String, cursoId: Int, fechaHoraEntregaMillis: Long, avisoMinAntes: Int?) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var selectedCurso by remember { mutableStateOf<CursoEntity?>(null) }
    var fechaMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) } // FECHA a 00:00
    var hora by remember { mutableStateOf(9) }     // por defecto 09:00
    var minuto by remember { mutableStateOf(0) }
    var cursoExpanded by remember { mutableStateOf(false) }

    // ----- Recordatorio -----
    var recordatorioOn by remember { mutableStateOf(true) }
    val opciones by remember(fechaMillis) {
        mutableStateOf(
            fechaMillis?.let { reminderOptions(it) } ?: emptyList()
        )
    }
    var idxOpcion by remember { mutableStateOf(0) }
    LaunchedEffect(opciones) { if (opciones.isNotEmpty()) idxOpcion = 0 }

    // Modo: predefinido vs personalizado
    var usePersonalizado by remember { mutableStateOf(false) }
    var customCountText by remember { mutableStateOf("") }
    var unitExpanded by remember { mutableStateOf(false) }
    var customUnit by remember { mutableStateOf(ReminderUnit.DIAS) } // default: días

    val fechaHoraEntrega = remember(fechaMillis, hora, minuto) {
        if (fechaMillis != null) combineDateAndTime(fechaMillis!!, hora, minuto) else 0L
    }
    val canSave = remember(desc, selectedCurso, fechaHoraEntrega) {
        desc.isNotBlank() && selectedCurso != null && fechaHoraEntrega > System.currentTimeMillis()
    }

    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    error = null
                    val c = selectedCurso ?: run { error = "Selecciona un curso"; return@TextButton }
                    if (fechaMillis == null) { error = "Selecciona una fecha"; return@TextButton }
                    if (fechaHoraEntrega <= System.currentTimeMillis()) {
                        error = "La fecha/hora debe ser futura"; return@TextButton
                    }

                    // ✨ Cálculo del aviso dinámico
                    val aviso: Int? = if (recordatorioOn) {
                        if (usePersonalizado) {
                            val count = customCountText.toIntOrNull()
                            if (count == null || count <= 0) {
                                error = "Ingresa una cantidad válida"; return@TextButton
                            }
                            minutesFrom(count, customUnit)
                        } else {
                            opciones.getOrNull(idxOpcion)?.second
                        }
                    } else null

                    // Validación: el recordatorio debe ser antes de la entrega
                    aviso?.let { min ->
                        val triggerAt = fechaHoraEntrega - min * 60_000L
                        if (triggerAt <= System.currentTimeMillis()) {
                            error = "El recordatorio debe ser antes de la entrega"; return@TextButton
                        }
                    }

                    onSave(desc.trim(), c.id, fechaHoraEntrega, aviso)
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

                // Curso
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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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

                // Fecha
                FechaField(
                    label = "Fecha de entrega",
                    valueMillis = fechaMillis,
                    onDatePicked = { picked -> fechaMillis = picked }
                )

                // Hora
                HoraField(
                    label = "Hora de entrega",
                    enabled = fechaMillis != null,
                    hour = hora,
                    minute = minuto,
                    onTimePicked = { h, m -> hora = h; minuto = m }
                )

                // Recordatorio (ON/OFF)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recordatorio")
                    Switch(checked = recordatorioOn, onCheckedChange = { recordatorioOn = it })
                }

                // Modo: Predefinido vs Personalizado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { usePersonalizado = false },
                        label = { Text("Predefinido") },
                        leadingIcon = {
                            if (!usePersonalizado) Icon(Icons.Outlined.Check, contentDescription = null)
                        },
                        enabled = recordatorioOn
                    )
                    AssistChip(
                        onClick = { usePersonalizado = true },
                        label = { Text("Personalizado") },
                        leadingIcon = {
                            if (usePersonalizado) Icon(Icons.Outlined.Check, contentDescription = null)
                        },
                        enabled = recordatorioOn
                    )
                }

                // --- PREDEFINIDO: igual que antes ---
                if (recordatorioOn && !usePersonalizado) {
                    var remExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = remExpanded,
                        onExpandedChange = { remExpanded = !remExpanded }
                    ) {
                        OutlinedTextField(
                            value = opciones.getOrNull(idxOpcion)?.first ?: "Sin opciones",
                            onValueChange = {},
                            readOnly = true,
                            enabled = opciones.isNotEmpty(),
                            label = { Text("Avisarme") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = remExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = remExpanded,
                            onDismissRequest = { remExpanded = false }
                        ) {
                            opciones.forEachIndexed { i, (label, _) ->
                                DropdownMenuItem(
                                    enabled = true,
                                    text = { Text(label) },
                                    onClick = { idxOpcion = i; remExpanded = false }
                                )
                            }
                        }
                    }
                }

                // --- PERSONALIZADO: número + unidad (min/horas/días) ---
                if (recordatorioOn && usePersonalizado) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customCountText,
                            onValueChange = { if (it.all { ch -> ch.isDigit() }) customCountText = it },
                            label = { Text("Cantidad") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        ExposedDropdownMenuBox(
                            expanded = unitExpanded,
                            onExpandedChange = { unitExpanded = !unitExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = customUnit.label(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Unidad") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false }
                            ) {
                                ReminderUnit.values().forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt.label()) },
                                        onClick = { customUnit = opt; unitExpanded = false }
                                    )
                                }
                            }
                        }
                    }

                    val _customMinutes = remember(customCountText, customUnit) {
                        customCountText.toIntOrNull()?.let { minutesFrom(it, customUnit) }
                    }
                    val hint = _customMinutes?.let { "Aviso: ${humanizeMinutes(it)} antes" } ?: " "
                    Text(
                        hint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Error (si lo hay)
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}
