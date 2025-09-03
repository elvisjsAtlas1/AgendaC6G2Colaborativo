package com.example.agendaencarta2004v3.actividades.ui

import android.app.DatePickerDialog
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.agendaencarta2004v3.actividades.viewmodel.ActividadViewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel
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
    var expandedCurso by remember { mutableStateOf(false) }
    var selectedCurso by remember { mutableStateOf<CursoEntity?>(null) } // 👈 el curso elegido

    var desc by remember { mutableStateOf("") }
    var cursoSeleccionado by remember { mutableStateOf<Int?>(null) }
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
        Text("Agregar Actividad", style = MaterialTheme.typography.titleLarge)

        // 🔹 Descripción
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

            // 🔹 Selector de Curso
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

        Spacer(Modifier.height(8.dp))

        // 🔹 Fecha de entrega (por ahora un botón que pone la fecha actual)
        Button(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
            Text("Seleccionar fecha")
        }

        Text("Fecha entrega: ${dateFormat.format(Date(fechaEntrega))}")

        Spacer(Modifier.height(16.dp))

        // 🔹 Guardar actividad
        Button(
            onClick = {
                Log.d("ActividadUI", "Botón Guardar clickeado. Desc: $desc, Curso: ${selectedCurso?.id}, Fecha: $fechaEntrega")

                if (desc.isNotBlank() && selectedCurso != null) {
                    actividadViewModel.addActividad(desc, selectedCurso!!.id, fechaEntrega)
                    Log.d("ActividadUI", "Se llamó a addActividad con éxito")

                    // Limpiar campos después de guardar
                    desc = ""
                    selectedCurso = null
                } else {
                    Log.d("ActividadUI", "No se guardó la actividad: campos incompletos")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Actividad")
        }


        Spacer(Modifier.height(24.dp))

        // 🔹 Lista de actividades
        Text("Actividades", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(actividades) { act ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(act.descripcion, style = MaterialTheme.typography.bodyLarge)
                        val cursoNombre = cursos.find { it.id == act.cursoId }?.nombre ?: "Curso desconocido"
                        Text("Curso: $cursoNombre")
                        Text("Entrega: ${Date(act.fechaEntrega)}")
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
    }
}
