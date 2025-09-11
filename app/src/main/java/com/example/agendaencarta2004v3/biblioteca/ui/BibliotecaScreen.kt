package com.example.agendaencarta2004v3.biblioteca.ui

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.School
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliotecaScreen(bibliotecaViewModel: BibliotecaViewModel) {
    var nombreCurso by remember { mutableStateOf("") }
    val cursos by bibliotecaViewModel.cursos.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca") }
                // 👇 sin acciones
            )
        }
    ) { inner ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize().padding(inner)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ---- Botón compacto para desplegar el formulario de CURSO ----
                FilledTonalButton(
                    onClick = { showForm = !showForm },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (showForm) "Ocultar formulario" else "Añadir curso")
                }

                // ---- Formulario de curso (colapsable) ----
                AnimatedVisibility(visible = showForm) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = nombreCurso,
                                    onValueChange = { nombreCurso = it },
                                    label = { Text("Nombre del curso") },
                                    leadingIcon = { Icon(Icons.Outlined.School, null) },
                                    modifier = Modifier.weight(1f)
                                )
                                FilledTonalButton(
                                    onClick = {
                                        if (nombreCurso.isNotBlank()) {
                                            bibliotecaViewModel.agregarCurso(nombreCurso.trim())
                                            nombreCurso = ""
                                            showForm = false
                                        }
                                    }
                                ) {
                                    Icon(Icons.Outlined.Check, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }

                // ---- Lista de cursos ----
                if (cursos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aún no has agregado cursos.")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(cursos, key = { it.id }) { curso ->
                            CursoItem(curso, bibliotecaViewModel)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CursoItem(curso: CursoEntity, viewModel: BibliotecaViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var nombreSemana by remember { mutableStateOf("") }
    val semanas by viewModel.getSemanasByCurso(curso.id).collectAsState(initial = emptyList())

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {

            // Header del curso
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📚 ${curso.nombre}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {

                    // Botón compacto para desplegar form de SEMANA
                    FilledTonalButton(
                        onClick = { showForm = !showForm },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (showForm) "Ocultar formulario" else "Añadir semana")
                    }

                    // Form de semana (colapsable)
                    AnimatedVisibility(visible = showForm) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = nombreSemana,
                                onValueChange = { nombreSemana = it },
                                label = { Text("Nombre de la semana") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                if (nombreSemana.isNotBlank()) {
                                    viewModel.agregarSemana(curso.id, nombreSemana.trim())
                                    nombreSemana = ""
                                    showForm = false
                                }
                            }) { Text("Guardar") }
                        }
                    }

                    // Lista de semanas
                    semanas.forEach { semana ->
                        SemanaItem(semana, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialCard(
    material: MaterialEntity,
    onOpen: (Uri, String) -> Unit,
    onOpenUrl: (String) -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.PushPin, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(material.info, style = MaterialTheme.typography.bodyLarge)
            }

            material.uriDoc?.let { str ->
                val uri = Uri.parse(str)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onOpen(uri, "application/*") }.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.InsertDriveFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(uri.lastPathSegment ?: "Documento", color = MaterialTheme.colorScheme.primary)
                }
            }

            material.uriImg?.let { str ->
                val uri = Uri.parse(str)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onOpen(uri, "image/*") }.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Image, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(uri.lastPathSegment ?: "Imagen", color = MaterialTheme.colorScheme.primary)
                }
            }

            material.url?.let { link ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenUrl(link) }.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(link, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun SemanaItem(semana: SemanaEntity, viewModel: BibliotecaViewModel) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    // Form material (colapsable)
    var showFormMat by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var uriDoc by remember { mutableStateOf<Uri?>(null) }
    var uriImg by remember { mutableStateOf<Uri?>(null) }

    val materiales by viewModel.getMaterialesBySemana(semana.id)
        .collectAsState(initial = emptyList())

    // Launchers uniformes
    val docPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uriDoc = uri }

    val imgPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uriImg = uri }

    val pillHeight = 44.dp

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {

            // Header de semana (expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.CalendarViewWeek, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    semana.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {

                    // Botón para mostrar/ocultar formulario de Material
                    FilledTonalButton(
                        onClick = { showFormMat = !showFormMat },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (showFormMat) "Ocultar formulario" else "Agregar material")
                    }

                    // -------- Formulario de material (colapsable) --------
                    AnimatedVisibility(visible = showFormMat) {
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Descripción
                                OutlinedTextField(
                                    value = info,
                                    onValueChange = { info = it },
                                    label = { Text("Descripción / Info") },
                                    leadingIcon = { Icon(Icons.Outlined.Description, null) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Botones Documento / Imagen (uniformes)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { docPicker.launch(arrayOf("*/*")) },
                                        shape = MaterialTheme.shapes.extraLarge,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(pillHeight),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Icon(Icons.Outlined.AttachFile, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Documento", maxLines = 1, softWrap = false)
                                    }

                                    OutlinedButton(
                                        onClick = { imgPicker.launch("image/*") },
                                        shape = MaterialTheme.shapes.extraLarge,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(pillHeight),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Icon(Icons.Outlined.Image, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Imagen", maxLines = 1, softWrap = false)
                                    }
                                }

                                // Nombres seleccionados (opcional)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    uriDoc?.let {
                                        Text(
                                            "📄 ${it.lastPathSegment ?: "Documento seleccionado"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    uriImg?.let {
                                        Text(
                                            "🖼 ${it.lastPathSegment ?: "Imagen seleccionada"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Enlace
                                OutlinedTextField(
                                    value = url,
                                    onValueChange = { url = it },
                                    label = { Text("Enlace (opcional)") },
                                    leadingIcon = { Icon(Icons.Outlined.Link, null) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Acciones (Cancelar / Guardar) – mismo estilo y altura
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            info = ""; url = ""; uriDoc = null; uriImg = null
                                            showFormMat = false
                                        },
                                        shape = MaterialTheme.shapes.extraLarge,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(pillHeight),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Text("Cancelar", maxLines = 1, softWrap = false)
                                    }

                                    Button(
                                        onClick = {
                                            if (info.isNotBlank()) {
                                                viewModel.agregarMaterial(
                                                    semanaId = semana.id,
                                                    info = info.trim(),
                                                    uriDoc = uriDoc?.toString(),
                                                    uriImg = uriImg?.toString(),
                                                    url = url.takeIf { it.isNotBlank() }
                                                )
                                                info = ""; url = ""; uriDoc = null; uriImg = null
                                                showFormMat = false
                                            }
                                        },
                                        shape = MaterialTheme.shapes.extraLarge,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(pillHeight),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        Icon(Icons.Outlined.Save, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Guardar", maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }

                    // -------- Lista de materiales --------
                    if (materiales.isEmpty()) {
                        Text(
                            "Sin materiales.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            materiales.forEach { material ->
                                MaterialCard(
                                    material = material,
                                    onOpen = { uri: Uri, mime: String ->
                                        abrirArchivo(context, uri, mime)
                                    },
                                    onOpenUrl = { link: String ->
                                        try {
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                            )
                                        } catch (_: Exception) {
                                            Toast.makeText(
                                                context,
                                                "No se puede abrir el enlace",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun SelectorArchivo(onArchivoSeleccionado: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(onArchivoSeleccionado)
    }
    OutlinedButton(onClick = { launcher.launch(arrayOf("*/*")) }) {
        Icon(Icons.Outlined.AttachFile, null); Spacer(Modifier.width(8.dp)); Text("Documento")
    }
}

@Composable
fun SelectorImagen(onImagenSeleccionada: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(onImagenSeleccionada)
    }
    OutlinedButton(onClick = { launcher.launch("image/*") }) {
        Icon(Icons.Outlined.Image, null); Spacer(Modifier.width(8.dp)); Text("Imagen")
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
