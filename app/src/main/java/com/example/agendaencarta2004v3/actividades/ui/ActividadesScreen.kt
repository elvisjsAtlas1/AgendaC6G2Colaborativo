package com.example.agendaencarta2004v3.actividades.ui

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agendaencarta2004v3.actividades.viewmodel.ActividadViewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel
import com.example.agendaencarta2004v3.core.materialTheme.DropdownMenuItemStyled
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun ActividadesScreen(
    actividadViewModel: ActividadViewModel,
    bibliotecaViewModel: BibliotecaViewModel
) {
    val actividades by actividadViewModel.actividades.collectAsState()
    val cursos by bibliotecaViewModel.cursos.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    var desc by remember { mutableStateOf("") }
    var selectedCurso by remember { mutableStateOf<CursoEntity?>(null) }
    var fechaEntrega by remember { mutableStateOf(System.currentTimeMillis()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            fechaEntrega = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(Modifier.padding(16.dp)) {
        // Botón desplegar formulario
        Button(
            onClick = { showForm = !showForm },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showForm) "Ocultar Formulario" else "➕ Añadir Actividad")
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
                Column(Modifier.padding(12.dp)) {
                    // 🔹 Descripción
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    // 🔹 Selector Curso
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
                            cursos.forEach { curso ->
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

                    // 🔹 Fecha de entrega
                    Button(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Seleccionar fecha")
                    }

                    Spacer(Modifier.height(12.dp))

                    // 🔹 Guardar actividad
                    Button(
                        onClick = {
                            if (desc.isNotBlank() && selectedCurso != null) {
                                actividadViewModel.addActividad(desc, selectedCurso!!.id, fechaEntrega)
                                desc = ""
                                selectedCurso = null
                                showForm = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Actividad")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Lista de actividades
        Text("Actividades", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(actividades) { act ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(act.descripcion, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            val cursoNombre = cursos.find { it.id == act.cursoId }?.nombre ?: "Curso desconocido"
                            Text("Curso: $cursoNombre", style = MaterialTheme.typography.bodyMedium)
                            Text("Entrega: ${dateFormat.format(Date(act.fechaEntrega))}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Checkbox(
                            checked = act.hecho,
                            onCheckedChange = { checked -> actividadViewModel.toggleActividad(act, checked) }
                        )
                    }
                }
            }
        }
    }
}
