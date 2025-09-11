package com.example.agendaencarta2004v3.agenda.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.agendaencarta2004v3.agenda.entity.EventoEntity
import com.example.agendaencarta2004v3.agenda.viewmodel.EventoViewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel


@Composable
fun AgendaScreen(
    agendaViewModel: EventoViewModel,
    bibliotecaViewModel: BibliotecaViewModel
) {
    val cursos by bibliotecaViewModel.cursos.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    // 🎨 Toma colores del tema oscuro
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)  // ⬅️ fondo oscuro consistente
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Agenda de Cursos",
            style = MaterialTheme.typography.titleLarge,
            color = cs.onBackground
        )

        // Botón principal: usa Tonal para que no “salte” tanto en dark
        FilledTonalButton(onClick = { showSheet = true }) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar Evento")
        }

        // Timeline
        TablaEventos(
            agendaViewModel = agendaViewModel,
            cursosDisponibles = cursos
        )
    }

    if (showSheet) {
        FormAgregarEventoSheet(
            cursosDisponibles = cursos,
            onDismiss = { showSheet = false },
            onSave = { cursoId, dia, hIni, hFin, aula ->
                agendaViewModel.agregarEvento(cursoId, dia, hIni, hFin, aula)
                showSheet = false
            }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    label: String,
    timeText: String,             // "HH:mm"
    supportingText: String? = null,
    onTimeChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Outlined.Schedule, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("$label: $timeText")
    }

    if (supportingText != null) {
        Text(
            text = supportingText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
    }

    if (showDialog) {
        val (hInit, mInit) = remember(timeText) {
            runCatching {
                val parts = timeText.split(":")
                val h = parts.getOrNull(0)?.toInt() ?: 0
                val m = parts.getOrNull(1)?.toInt() ?: 0
                h.coerceIn(0, 23) to m.coerceIn(0, 59)
            }.getOrDefault(0 to 0)
        }

        val state = rememberTimePickerState(
            initialHour = hInit,
            initialMinute = mInit,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(String.format("%02d:%02d", state.hour, state.minute))
                    showDialog = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } },
            title = { Text(label) },
            text = { TimePicker(state = state, layoutType = TimePickerLayoutType.Vertical) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAgregarEventoSheet(
    cursosDisponibles: List<CursoEntity>,
    onDismiss: () -> Unit,
    onSave: (cursoId: Int, dia: String, horaInicio: String, horaFin: String, aula: String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var curso by remember { mutableStateOf<CursoEntity?>(null) }
    var dia by remember { mutableStateOf("Lunes") }
    var horaInicio by remember { mutableStateOf("08:00") }
    var horaFin by remember { mutableStateOf("09:00") }
    var aula by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Curso
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
                OutlinedTextField(
                    value = curso?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Curso") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                    cursosDisponibles.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.nombre) },
                            onClick = { curso = c; expanded = false }
                        )
                    }
                }
            }

            // Día
            var diaExpanded by remember { mutableStateOf(false) }
            val dias = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")
            ExposedDropdownMenuBox(diaExpanded, { diaExpanded = !diaExpanded }) {
                OutlinedTextField(
                    value = dia, onValueChange = {}, readOnly = true,
                    label = { Text("Día") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(diaExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(diaExpanded, onDismissRequest = { diaExpanded = false }) {
                    dias.forEach { d -> DropdownMenuItem(text = { Text(d) }, onClick = { dia = d; diaExpanded = false }) }
                }
            }

            // Horas
            TimeField("Hora inicio", horaInicio, supportingText = "Formato 24h") { horaInicio = it }
            TimeField("Hora fin", horaFin, supportingText = "Debe ser posterior al inicio") { horaFin = it }

            // Aula
            OutlinedTextField(value = aula, onValueChange = { aula = it }, label = { Text("Aula") }, modifier = Modifier.fillMaxWidth())

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Button(
                    onClick = {
                        error = null
                        val c = curso ?: run { error = "Selecciona un curso"; return@Button }
                        if (!isStartBeforeEnd(horaInicio, horaFin)) {
                            error = "La hora de inicio debe ser menor que la de fin"; return@Button
                        }
                        onSave(c.id, dia, horaInicio, horaFin, aula)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}



// Helper: valida "HH:mm"
private fun isStartBeforeEnd(inicio: String, fin: String): Boolean {
    fun toMin(s: String) = s.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
    return toMin(inicio) < toMin(fin)
}



private val diasOrden = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sabado","Domingo")

private fun toMinutes(hhmm: String): Int =
    hhmm.split(":").let { it[0].toInt() * 60 + it[1].toInt() }

private fun clamp(v: Int, min: Int, max: Int) = v.coerceIn(min, max)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TablaEventos(
    agendaViewModel: EventoViewModel,
    cursosDisponibles: List<CursoEntity> = emptyList(),
    startHour: Int = 5,
    endHour: Int = 24,
    minutesPerDp: Float = 1.6f,
    dayWidth: Dp = 140.dp
) {
    val eventos by agendaViewModel.eventos.collectAsState()

    val cs = MaterialTheme.colorScheme
    val outline = cs.outlineVariant // líneas/contornos sutiles
    val surface = cs.surface       // tarjetas / columnas
    val surfaceAlt = cs.surfaceVariant

    var editarEvento by remember { mutableStateOf<EventoEntity?>(null) }
    var confirmarEliminar by remember { mutableStateOf<EventoEntity?>(null) }

    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    val totalMin = (endHour - startHour) * 60
    val gridHeight = (totalMin / minutesPerDp).dp
    val hourStepY = (60 / minutesPerDp).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.background)      // ⬅️ sin azules
            .padding(12.dp)
    ) {
        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Hora", color = cs.onBackground, style = MaterialTheme.typography.labelLarge)
            }
            Row(
                modifier = Modifier
                    .horizontalScroll(hScroll)
                    .height(36.dp)
            ) {
                diasOrden.forEach { dia ->
                    Box(
                        modifier = Modifier
                            .width(dayWidth)
                            .fillMaxHeight()
                            .padding(horizontal = 4.dp)
                            .background(surfaceAlt, RoundedCornerShape(10.dp))
                            .border(1.dp, outline, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dia,
                            color = cs.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row {
            // Columna de horas
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .verticalScroll(vScroll)
                    .height(gridHeight)
                    .background(surface, RoundedCornerShape(10.dp))
                    .border(1.dp, outline, RoundedCornerShape(10.dp))
            ) {
                for (h in startHour..endHour) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(hourStepY)
                            .padding(top = 2.dp, start = 6.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = String.format("%02d:00", h),
                            color = cs.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 14.dp, end = 4.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(outline)
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Grid de días
            Box(
                modifier = Modifier
                    .horizontalScroll(hScroll)
                    .verticalScroll(vScroll)
                    .height(gridHeight)
            ) {
                Row {
                    diasOrden.forEach { dia ->
                        Box(
                            modifier = Modifier
                                .width(dayWidth)
                                .height(gridHeight)
                                .padding(horizontal = 4.dp)
                                .background(surface, RoundedCornerShape(10.dp))
                                .border(1.dp, outline, RoundedCornerShape(10.dp))
                        ) {
                            // Líneas de hora
                            Column(
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(horizontal = 4.dp)
                            ) {
                                for (h in startHour..endHour) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(hourStepY)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(outline)
                                        )
                                    }
                                }
                            }

                            // Eventos
                            val lista = remember(eventos, dia) {
                                eventos.filter { it.dia == dia }
                                    .sortedBy { toMinutes(it.horaInicio) }
                            }
                            lista.forEach { e ->
                                val iniMin = toMinutes(e.horaInicio).coerceIn(startHour * 60, endHour * 60)
                                val finMin = toMinutes(e.horaFin).coerceIn(startHour * 60, endHour * 60)
                                val dur = maxOf(finMin - iniMin, 10)
                                val offsetY = ((iniMin - startHour * 60) / minutesPerDp).dp
                                val height = (dur / minutesPerDp).dp

                                val cursoNombre = cursosDisponibles.firstOrNull { it.id == e.cursoId }?.nombre ?: "Curso"

                                Box(
                                    modifier = Modifier
                                        .offset(y = offsetY)
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth()
                                        .height(height)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(cs.primary)                 // ⬅️ color principal
                                        .combinedClickable(
                                            onClick = { editarEvento = e },
                                            onLongClick = { confirmarEliminar = e }
                                        )
                                        .border(1.dp, cs.primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            cursoNombre,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = cs.onPrimary,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            "${e.horaInicio} - ${e.horaFin}" +
                                                    (if (e.aula.isNotBlank()) " • Aula ${e.aula}" else ""),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = cs.onPrimary.copy(alpha = 0.85f),
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // diálogos (sin cambios de color; heredan del tema)
    if (editarEvento != null) {
        EditarEventoDialog(
            evento = editarEvento!!,
            cursosDisponibles = cursosDisponibles,
            onDismiss = { editarEvento = null },
            onSave = { actualizado ->
                agendaViewModel.actualizarEvento(actualizado)
                editarEvento = null
            }
        )
    }
    if (confirmarEliminar != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = null },
            confirmButton = {
                TextButton(onClick = {
                    agendaViewModel.eliminarEvento(confirmarEliminar!!)
                    confirmarEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = null }) { Text("Cancelar") } },
            title = { Text("Eliminar evento") },
            text = {
                val e = confirmarEliminar!!
                val cursoNombre = cursosDisponibles.firstOrNull { it.id == e.cursoId }?.nombre ?: "Curso"
                Text("¿Eliminar $cursoNombre (${e.dia} ${e.horaInicio}-${e.horaFin})?")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEventoDialog(
    evento: EventoEntity,
    cursosDisponibles: List<CursoEntity>,
    onDismiss: () -> Unit,
    onSave: (EventoEntity) -> Unit
) {
    var curso by remember { mutableStateOf(cursosDisponibles.firstOrNull { it.id == evento.cursoId }) }
    var dia by remember { mutableStateOf(evento.dia) }
    var horaInicio by remember { mutableStateOf(evento.horaInicio) }
    var horaFin by remember { mutableStateOf(evento.horaFin) }
    var aula by remember { mutableStateOf(evento.aula) }

    val dias = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    evento.copy(
                        cursoId = curso?.id ?: evento.cursoId,
                        dia = dia,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        aula = aula
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Editar evento") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Curso
                var expCurso by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expCurso, onExpandedChange = { expCurso = !expCurso }) {
                    OutlinedTextField(
                        value = curso?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expCurso) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expCurso, onDismissRequest = { expCurso = false }) {
                        cursosDisponibles.forEach {
                            DropdownMenuItem(
                                text = { Text(it.nombre) },
                                onClick = { curso = it; expCurso = false }
                            )
                        }
                    }
                }

                // Día
                var expDia by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expDia, onExpandedChange = { expDia = !expDia }) {
                    OutlinedTextField(
                        value = dia, onValueChange = {},
                        readOnly = true, label = { Text("Día") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expDia) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expDia, onDismissRequest = { expDia = false }) {
                        dias.forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = { dia = it; expDia = false })
                        }
                    }
                }

                // Horarios con TimePicker
                TimeField("Hora inicio", horaInicio, onTimeChange = { horaInicio = it })
                TimeField("Hora fin", horaFin, onTimeChange = { horaFin = it })

                OutlinedTextField(
                    value = aula,
                    onValueChange = { aula = it },
                    label = { Text("Aula") },
                    leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}


