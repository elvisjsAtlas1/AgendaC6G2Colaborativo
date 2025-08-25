package com.example.agendaencarta2004v3.agenda.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendaencarta2004v3.agenda.viewmodel.AgendaViewModel
import com.example.agendaencarta2004v3.biblioteca.model.Curso
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel


// FormAgregarCurso.kt (o dentro de AgendaScreen.kt, pero fuera de AgendaScreen)
@Composable
fun AgendaScreen(
    agendaViewModel: AgendaViewModel = viewModel(),
    bibliotecaViewModel: BibliotecaViewModel = viewModel()
) {
    val cursosBiblioteca by bibliotecaViewModel.cursos.collectAsState()
    val eventos by agendaViewModel.eventos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Agenda de Cursos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        var mostrarFormulario by remember { mutableStateOf(false) }
        Button(onClick = { mostrarFormulario = !mostrarFormulario }) {
            Text(if (mostrarFormulario) "Cerrar Formulario" else "+ Agregar Evento")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (mostrarFormulario) {
            FormAgregarEvento(
                agendaViewModel = agendaViewModel,
                cursosDisponibles = cursosBiblioteca // pasamos los cursos de la biblioteca
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pasamos la lista de eventos y cursos a la tabla
        TablaEventos(
            agendaViewModel = agendaViewModel,
            cursosDisponibles = bibliotecaViewModel.cursos.collectAsState().value
        )
    }
}


@Composable
fun FormAgregarEvento(
    agendaViewModel: AgendaViewModel,
    cursosDisponibles: List<Curso> = emptyList() // cursos de la biblioteca
) {
    var selectedCurso by remember { mutableStateOf<Curso?>(null) }
    var dia by remember { mutableStateOf("Lunes") }
    var horaInicio by remember { mutableStateOf("08:00") }
    var horaFin by remember { mutableStateOf("09:00") }
    var aula by remember { mutableStateOf("") }

    var expandedCurso by remember { mutableStateOf(false) }
    var expandedDia by remember { mutableStateOf(false) }
    var expandedHoraInicio by remember { mutableStateOf(false) }
    var expandedHoraFin by remember { mutableStateOf(false) }

    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    val horas = listOf("08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00")

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

        // Selección de curso
        Box {
            OutlinedButton(onClick = { expandedCurso = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedCurso?.nombre ?: "Seleccionar Curso")
            }
            DropdownMenu(
                expanded = expandedCurso,
                onDismissRequest = { expandedCurso = false }
            ) {
                cursosDisponibles.forEach { curso ->
                    DropdownMenuItem(
                        text = { Text(curso.nombre) },
                        onClick = {
                            selectedCurso = curso
                            expandedCurso = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selección de día
        Box {
            OutlinedButton(onClick = { expandedDia = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Día: $dia")
            }
            DropdownMenu(expanded = expandedDia, onDismissRequest = { expandedDia = false }) {
                dias.forEach { d ->
                    DropdownMenuItem(
                        text = { Text(d) },
                        onClick = {
                            dia = d
                            expandedDia = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selección de hora inicio
        Box {
            OutlinedButton(onClick = { expandedHoraInicio = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Hora Inicio: $horaInicio")
            }
            DropdownMenu(expanded = expandedHoraInicio, onDismissRequest = { expandedHoraInicio = false }) {
                horas.forEach { h ->
                    DropdownMenuItem(
                        text = { Text(h) },
                        onClick = {
                            horaInicio = h
                            expandedHoraInicio = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selección de hora fin
        Box {
            OutlinedButton(onClick = { expandedHoraFin = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Hora Fin: $horaFin")
            }
            DropdownMenu(expanded = expandedHoraFin, onDismissRequest = { expandedHoraFin = false }) {
                horas.forEach { h ->
                    DropdownMenuItem(
                        text = { Text(h) },
                        onClick = {
                            horaFin = h
                            expandedHoraFin = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Aula
        OutlinedTextField(
            value = aula,
            onValueChange = { aula = it },
            label = { Text("Aula") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Guardar
        Button(
            onClick = {
                selectedCurso?.let {
                    agendaViewModel.agregarEvento(it.id, dia, horaInicio, horaFin, aula)
                    // Limpiar campos
                    selectedCurso = null
                    dia = "Lunes"
                    horaInicio = "08:00"
                    horaFin = "09:00"
                    aula = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}






@Composable
fun TablaEventos(agendaViewModel: AgendaViewModel, cursosDisponibles: List<Curso> = emptyList()) {
    val eventos by agendaViewModel.eventos.collectAsState()
    val cursos = cursosDisponibles.ifEmpty { agendaViewModel.cursos.collectAsState().value }

    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    val horas = listOf("08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00")

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        // Cabecera con días
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(60.dp)) // espacio para columna de horas
                dias.forEach { dia ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .border(1.dp, Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(dia, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Filas de horas
        items(horas) { hora ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Columna de hora
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                        .border(1.dp, Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(hora, style = MaterialTheme.typography.bodyMedium)
                }

                // Columnas de días con eventos
                dias.forEach { dia ->
                    val eventosEnCelda = eventos.filter { it.dia == dia && it.horaInicio == hora }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(maxOf(60.dp, (eventosEnCelda.size * 20).dp)) // Ajusta altura según eventos
                            .border(1.dp, Color.Gray)
                            .padding(4.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column {
                            eventosEnCelda.forEach { evento ->
                                val cursoNombre = cursos.firstOrNull { it.id == evento.cursoId }?.nombre ?: "Curso no encontrado"
                                Text(
                                    "$cursoNombre (${evento.aula}) ${evento.horaInicio}-${evento.horaFin}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



