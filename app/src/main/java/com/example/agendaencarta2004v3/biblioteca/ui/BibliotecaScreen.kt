package com.example.agendaencarta2004v3.biblioteca.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import com.example.agendaencarta2004v3.biblioteca.entity.SemanaEntity
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModelFactory

@Composable
fun BibliotecaScreen(bibliotecaViewModel: BibliotecaViewModel) {
    var nombreCurso by remember { mutableStateOf("") }
    val cursos by bibliotecaViewModel.cursos.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = nombreCurso,
                onValueChange = { nombreCurso = it },
                label = { Text("Nombre del curso") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (nombreCurso.isNotBlank()) {
                        bibliotecaViewModel.agregarCurso(nombreCurso)
                        nombreCurso = ""
                    }
                }
            ) {
                Text("Agregar Curso")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(cursos) { curso ->
                CursoItem(curso, bibliotecaViewModel)
            }
        }

    }
}


@Composable
fun CursoItem(curso: CursoEntity, viewModel: BibliotecaViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var nombreSemana by remember { mutableStateOf("") }

    // 🔹 Obtener las semanas desde Room (flow -> collectAsState)
    val semanas by viewModel.getSemanasByCurso(curso.id).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text("📚 ${curso.nombre}", fontWeight = FontWeight.Bold)

        if (expanded) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = nombreSemana,
                    onValueChange = { nombreSemana = it },
                    label = { Text("Nombre de la semana") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (nombreSemana.isNotBlank()) {
                            viewModel.agregarSemana(curso.id, nombreSemana)
                            nombreSemana = "" // limpiar
                        }
                    }
                ) {
                    Text("➕ Añadir Semana")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            semanas.forEach { semana ->
                SemanaItem(semana, viewModel)
            }
        }
    }
}


@Composable
fun SemanaItem(semana: SemanaEntity, viewModel: BibliotecaViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    // 🔹 Obtener materiales de la BD
    val materiales by viewModel.getMaterialesBySemana(semana.id).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp)
            .clickable { expanded = !expanded }
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text("📅 ${semana.titulo}", fontWeight = FontWeight.Medium)

        if (expanded) {
            Spacer(modifier = Modifier.height(4.dp))

            Button(onClick = { mostrarDialogo = true }) {
                Text("➕ Agregar Material")
            }

            Spacer(modifier = Modifier.height(8.dp))

            materiales.forEach { material ->
                MaterialItem(material)
            }
        }
    }

    if (mostrarDialogo) {
        DialogAgregarMaterial(
            semanaId = semana.id,
            viewModel = viewModel,
            onDismiss = { mostrarDialogo = false }
        )
    }
}


@Composable
fun MaterialItem(material: MaterialEntity) {
    val icon = when (material.tipo) {
        "Documento" -> "📄"
        "Imagen" -> "🖼"
        "Enlace" -> "🔗"
        else -> "❓"
    }

    Text(
        text = "$icon ${material.titulo}",
        modifier = Modifier.padding(start = 32.dp, top = 4.dp)
    )
}



@Composable
fun DialogAgregarMaterial(
    semanaId: Int,
    viewModel: BibliotecaViewModel,
    onDismiss: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var enlace by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Documento") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Material") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = enlace,
                    onValueChange = { enlace = it },
                    label = { Text("Enlace / URI") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de tipo
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expanded = true }) {
                        Text(tipo)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Documento") },
                            onClick = { tipo = "Documento"; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Imagen") },
                            onClick = { tipo = "Imagen"; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Enlace") },
                            onClick = { tipo = "Enlace"; expanded = false }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (titulo.isNotBlank()) {
                    viewModel.agregarMaterial(semanaId, titulo, tipo, uri = if (tipo != "Enlace") enlace else null, url = if (tipo == "Enlace") enlace else null)
                    onDismiss()
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
