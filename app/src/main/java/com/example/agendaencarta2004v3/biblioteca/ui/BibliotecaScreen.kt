package com.example.agendaencarta2004v3.biblioteca.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import com.example.agendaencarta2004v3.biblioteca.entity.SemanaEntity
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModelFactory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults


@Composable
fun BibliotecaScreen(bibliotecaViewModel: BibliotecaViewModel) {
    var nombreCurso by remember { mutableStateOf("") }
    val cursos by bibliotecaViewModel.cursos.collectAsState()

    // 👇 Controla si el formulario está abierto o cerrado
    var showForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        // 🔹 Botón para mostrar/ocultar formulario
        Button(
            onClick = { showForm = !showForm },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showForm) "Ocultar Formulario" else "➕ Agregar Curso")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Animación de despliegue del formulario
        AnimatedVisibility(visible = showForm) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = nombreCurso,
                        onValueChange = { nombreCurso = it },
                        label = { Text("Nombre del curso") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (nombreCurso.isNotBlank()) {
                                bibliotecaViewModel.agregarCurso(nombreCurso)
                                nombreCurso = ""
                                showForm = false // 👈 Oculta formulario después de guardar
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Lista de cursos
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
    var showForm by remember { mutableStateOf(false) }
    var nombreSemana by remember { mutableStateOf("") }

    // 🔹 Obtener las semanas desde Room (flow -> collectAsState)
    val semanas by viewModel.getSemanasByCurso(curso.id).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Encabezado del curso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📚 ${curso.nombre}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // 🔹 Contenido expandido
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {

                // Botón para mostrar formulario
                Button(
                    onClick = { showForm = !showForm },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showForm) "Ocultar Formulario" else "➕ Añadir Semana")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Formulario animado
                AnimatedVisibility(visible = showForm) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = nombreSemana,
                            onValueChange = { nombreSemana = it },
                            label = { Text("Nombre de la semana") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (nombreSemana.isNotBlank()) {
                                    viewModel.agregarSemana(curso.id, nombreSemana)
                                    nombreSemana = ""
                                    showForm = false // Oculta formulario tras guardar
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) {
                            Text("Guardar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Lista de semanas
                semanas.forEach { semana ->
                    SemanaItem(semana, viewModel)
                }
            }
        }
    }
}


@Composable
fun SemanaItem(semana: SemanaEntity, viewModel: BibliotecaViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val materiales by viewModel.getMaterialesBySemana(semana.id).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Encabezado desplegable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📅 ${semana.titulo}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {

                // Botón para mostrar/ocultar formulario
                Button(
                    onClick = { showForm = !showForm },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showForm) "Ocultar Formulario" else "➕ Agregar Material")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Formulario animado
                AnimatedVisibility(visible = showForm) {
                    Column {
                        var info by remember { mutableStateOf("") }
                        var url by remember { mutableStateOf("") }
                        var uriDoc by remember { mutableStateOf<Uri?>(null) }
                        var uriImg by remember { mutableStateOf<Uri?>(null) }

                        OutlinedTextField(
                            value = info,
                            onValueChange = { info = it },
                            label = { Text("Descripción / Info") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SelectorArchivo { uriDoc = it }
                        uriDoc?.let { Text("📄 Documento: ${it.lastPathSegment}") }

                        Spacer(modifier = Modifier.height(8.dp))

                        SelectorImagen { uriImg = it }
                        uriImg?.let { Text("🖼 Imagen: ${it.lastPathSegment}") }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            label = { Text("Enlace (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (info.isNotBlank()) {
                                    viewModel.agregarMaterial(
                                        semanaId = semana.id,
                                        info = info,
                                        uriDoc = uriDoc?.toString(),
                                        uriImg = uriImg?.toString(),
                                        url = url.takeIf { it.isNotEmpty() }
                                    )
                                    info = ""
                                    url = ""
                                    uriDoc = null
                                    uriImg = null
                                    showForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar materiales agregados

                materiales.forEach { material ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                            .clickable { /* acción opcional */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📌 ${material.info}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Documento
                            material.uriDoc?.let { uriString ->
                                val uri = Uri.parse(uriString)
                                val nombreArchivo = obtenerNombreArchivo(context, uri)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            abrirArchivo(context, uri, context.contentResolver.getType(uri) ?: "*/*")
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        "📄",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = nombreArchivo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Imagen
                            material.uriImg?.let { uriString ->
                                val uri = Uri.parse(uriString)
                                val nombreArchivo = obtenerNombreArchivo(context, uri)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            abrirArchivo(context, uri, context.contentResolver.getType(uri) ?: "*/*")
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        "🖼",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = nombreArchivo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Enlace
                            material.url?.let { url ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "No se puede abrir el enlace", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        "🔗",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = url,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
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

@Composable
fun DialogAgregarMaterial(
    semana: SemanaEntity,
    bibliotecaViewModel: BibliotecaViewModel,
    onDismiss: () -> Unit
) {
    var info by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var uriDoc by remember { mutableStateOf<Uri?>(null) }
    var uriImg by remember { mutableStateOf<Uri?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar Material",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 🔹 Info (obligatorio)
                OutlinedTextField(
                    value = info,
                    onValueChange = { info = it },
                    label = { Text("Descripción / Info") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 🔹 Documento
                Column {
                    SelectorArchivo { uri -> uriDoc = uri }
                    uriDoc?.let {
                        Text(
                            text = "📄 ${it.lastPathSegment ?: "Documento seleccionado"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // 🔹 Imagen
                Column {
                    SelectorImagen { uri -> uriImg = uri }
                    uriImg?.let {
                        Text(
                            text = "🖼 ${it.lastPathSegment ?: "Imagen seleccionada"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // 🔹 Enlace
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Enlace (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (info.isNotBlank()) {
                        bibliotecaViewModel.agregarMaterial(
                            semanaId = semana.id,
                            info = info,
                            uriDoc = uriDoc?.toString(),
                            uriImg = uriImg?.toString(),
                            url = url.takeIf { it.isNotEmpty() }
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}



@Composable
fun SelectorArchivo(onArchivoSeleccionado: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onArchivoSeleccionado(it) }
    }

    Button(
        onClick = { launcher.launch(arrayOf("*/*")) }, // puedes filtrar por tipo: "application/pdf", "text/*"
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("📄 Seleccionar Documento")
    }
}

@Composable
fun SelectorImagen(onImagenSeleccionada: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImagenSeleccionada(it) }
    }

    Button(
        onClick = { launcher.launch("image/*") },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("🖼 Seleccionar Imagen")
    }
}

@Composable
fun MaterialItem(material: MaterialEntity, context: Context = LocalContext.current) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        // Descripción / info
        Text("📌 ${material.info}", fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(4.dp))

        // Documento
        material.uriDoc?.let {
            val uri = Uri.parse(it)
            val nombre = obtenerNombreArchivo(context, uri)
            Text(
                "📄 $nombre",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    abrirArchivo(context, uri, "application/*")
                }
            )
        }

        // Imagen
        material.uriImg?.let {
            val uri = Uri.parse(it)
            Text(
                "🖼 Imagen",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    abrirArchivo(context, uri, "image/*")
                }
            )
        }

        // URL
        material.url?.let {
            Text(
                "🔗 $it",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            )
        }
    }
}

fun abrirArchivo(context: Context, uri: Uri, tipo: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, tipo)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show()
    }
}


fun obtenerNombreArchivo(context: Context, uri: Uri): String {
    var nombre: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index != -1) {
                nombre = it.getString(index)
            }
        }
    }
    return nombre ?: uri.lastPathSegment ?: "Archivo desconocido"
}
