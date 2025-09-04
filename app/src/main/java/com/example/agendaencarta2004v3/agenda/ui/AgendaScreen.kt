package com.example.agendaencarta2004v3.agenda.ui

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.agendaencarta2004v3.agenda.viewmodel.EventoViewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity

import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel
import com.example.agendaencarta2004v3.core.materialTheme.DropdownMenuItemStyled


// FormAgregarCurso.kt (o dentro de AgendaScreen.kt, pero fuera de AgendaScreen)
@Composable
fun AgendaScreen(
    agendaViewModel: EventoViewModel,
    bibliotecaViewModel: BibliotecaViewModel
) {
    val cursosBiblioteca by bibliotecaViewModel.cursos.collectAsState()
    val eventos by agendaViewModel.eventos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Agenda de Cursos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Botón + Agregar Evento con formulario animado
        FormAgregarEvento(
            agendaViewModel = agendaViewModel,
            cursosDisponibles = cursosBiblioteca
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Aquí tu tabla de eventos
        TablaEventos(
            agendaViewModel = agendaViewModel,
            cursosDisponibles = cursosBiblioteca
        )
    }
}

@Composable
fun FormAgregarEvento(
    agendaViewModel: EventoViewModel,
    cursosDisponibles: List<CursoEntity> = emptyList()
) {
    var showForm by remember { mutableStateOf(false) }

    var selectedCurso by remember { mutableStateOf<CursoEntity?>(null) }
    var dia by remember { mutableStateOf("Lunes") }
    var horaInicio by remember { mutableStateOf("08:00") }
    var horaFin by remember { mutableStateOf("09:00") }
    var aula by remember { mutableStateOf("") }

    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    val horas = listOf("08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00")

    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        // Botón desplegar formulario
        Button(
            onClick = { showForm = !showForm },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showForm) "Ocultar Formulario" else "➕ Agregar Evento")
        }

        Spacer(Modifier.height(8.dp))

        // Formulario animado
        AnimatedVisibility(visible = showForm) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    // Curso
                    var expandedCurso by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expandedCurso = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedCurso?.nombre ?: "Seleccionar Curso")
                        }

                        DropdownMenu(
                            expanded = expandedCurso,
                            onDismissRequest = { expandedCurso = false }
                        ) {
                            cursosDisponibles.forEach { curso ->
                                DropdownMenuItemStyled(
                                    text = curso.nombre,
                                    selected = selectedCurso == curso,
                                    onClick = {
                                        selectedCurso = curso
                                        expandedCurso = false
                                    }
                                )
                            }
                        }
                    }


                    Spacer(Modifier.height(8.dp))

                    // Día
                    var expandedDia by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expandedDia = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Día: $dia")
                        }
                        DropdownMenu(
                            expanded = expandedDia,
                            onDismissRequest = { expandedDia = false }
                        ) {
                            dias.forEach { d ->
                                DropdownMenuItemStyled(
                                    text = d,
                                    selected = dia == d,
                                    onClick = {
                                        dia = d
                                        expandedDia = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Hora Inicio
                    var expandedHoraInicio by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expandedHoraInicio = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Hora Inicio: $horaInicio")
                        }
                        DropdownMenu(
                            expanded = expandedHoraInicio,
                            onDismissRequest = { expandedHoraInicio = false }
                        ) {
                            horas.forEach { h ->
                                DropdownMenuItemStyled(
                                    text = h,
                                    selected = horaInicio == h,
                                    onClick = {
                                        horaInicio = h
                                        expandedHoraInicio = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Hora Fin
                    var expandedHoraFin by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expandedHoraFin = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Hora Fin: $horaFin")
                        }
                        DropdownMenu(
                            expanded = expandedHoraFin,
                            onDismissRequest = { expandedHoraFin = false }
                        ) {
                            horas.forEach { h ->
                                DropdownMenuItemStyled(
                                    text = h,
                                    selected = horaFin == h,
                                    onClick = {
                                        horaFin = h
                                        expandedHoraFin = false
                                    }
                                )
                            }
                        }

                    }

                    Spacer(Modifier.height(8.dp))

                    // Aula
                    OutlinedTextField(
                        value = aula,
                        onValueChange = { aula = it },
                        label = { Text("Aula") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    // Botón Guardar
                    Button(
                        onClick = {
                            selectedCurso?.let {
                                agendaViewModel.agregarEvento(it.id, dia, horaInicio, horaFin, aula)
                                selectedCurso = null
                                dia = "Lunes"
                                horaInicio = "08:00"
                                horaFin = "09:00"
                                aula = ""
                                showForm = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun TablaEventos(agendaViewModel: EventoViewModel, cursosDisponibles: List<CursoEntity> = emptyList()) {
    val eventos by agendaViewModel.eventos.collectAsState()
    val cursos = cursosDisponibles

    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    val horas = listOf("08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00")

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF023059)) // Fondo principal azul oscuro
    ) {
        // 🔹 Encabezado de días
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(60.dp))
                dias.forEach { dia ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .border(1.dp, Color(0xFF0388A6))
                            .background(Color(0xFF023E73)), // Azul medio
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dia,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                    }
                }
            }
        }
        // 🔹 Filas de horas + eventos
        items(horas) { hora ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                        .border(1.dp, Color(0xFF0388A6))
                        .background(Color(0xFF03658C)), // Azul
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        hora,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }

                dias.forEach { dia ->
                    val eventosEnCelda = eventos.filter { it.dia == dia && it.horaInicio == hora }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(maxOf(60.dp, (eventosEnCelda.size * 20).dp))
                            .border(1.dp, Color(0xFF0388A6))
                            .background(
                                if (eventosEnCelda.isNotEmpty()) Color(0xFF04ADBF) // Turquesa si hay evento
                                else Color(0xFF023059) // Fondo oscuro si vacío
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column {
                            eventosEnCelda.forEach { evento ->
                                val cursoNombre = cursos.firstOrNull { it.id == evento.cursoId }?.nombre
                                    ?: "Curso no encontrado"
                                Text(
                                    "$cursoNombre (${evento.aula}) ${evento.horaInicio}-${evento.horaFin}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




