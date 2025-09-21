package com.example.agendaencarta2004v3.biblioteca.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

/* ===================== BibliotecaScreen ===================== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliotecaScreen(bibliotecaViewModel: BibliotecaViewModel) {
    var nombreCurso by remember { mutableStateOf("") }
    val cursos by bibliotecaViewModel.cursos.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca") },
                actions = {
                    // Botón en la barra superior (derecha) alineado con el título
                    FilledTonalButton(
                        onClick = { showForm = !showForm },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (showForm) "Ocultar" else "Añadir curso")
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        // ❌ sin floatingActionButton
    ) { inner ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Formulario de curso (colapsable)
                AnimatedVisibility(visible = showForm) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
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
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Curso agregado")
                                            }
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

                // Lista de cursos (swipe-to-delete)
                if (cursos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aún no has agregado cursos.")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(cursos, key = { it.id }) { curso ->
                            CursoItemSwipe(
                                curso = curso,
                                viewModel = bibliotecaViewModel,
                                onSnack = { msg ->
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlatCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background, // mismo negro del fondo
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        elevation = CardDefaults.elevatedCardElevation( // sin sombras
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            draggedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            content = content
        )
    }
}

/* ===================== Curso (swipe-to-delete) ===================== */

@Composable
private fun CursoItemSwipe(
    curso: CursoEntity,
    viewModel: BibliotecaViewModel,
    onSnack: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var askConfirm by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { target ->
            when (target) {
                SwipeToDismissBoxValue.EndToStart -> {
                    // En lugar de eliminar directo, mostramos confirmación y cancelamos el swipe
                    askConfirm = true
                    false
                }
                else -> false
            }
        }
    )

    // Dialogo confirmación
    ConfirmDeleteDialog(
        open = askConfirm,
        title = "Eliminar curso",
        message = "¿Seguro que quieres eliminar \"${curso.nombre}\" y sus semanas/materiales?",
        onConfirm = {
            viewModel.eliminarCursoById(curso.id) { ok ->
                onSnack(if (ok) "Curso eliminado" else "No se pudo eliminar")
            }
        },
        onDismiss = { askConfirm = false }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val bg = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceVariant
            Box(
                Modifier.fillMaxSize().background(bg).padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) { Icon(Icons.Outlined.Delete, null) }
        },
        content = {
            FlatCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📚 ${curso.nombre}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f).clickable { expanded = !expanded },
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            null
                        )
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        SemanasList(
                            cursoId = curso.id,
                            viewModel = viewModel,
                            onSnack = onSnack
                        )
                    }
                }
            }
        }
    )
}

/* ===================== Semanas (con swipe-to-delete) ===================== */

@Composable
private fun SemanasList(
    cursoId: Int,
    viewModel: BibliotecaViewModel,
    onSnack: (String) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var nombreSemana by remember { mutableStateOf("") }
    val semanas by viewModel.getSemanasByCurso(cursoId).collectAsState(initial = emptyList())

    FilledTonalButton(onClick = { showForm = !showForm }, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Outlined.Add, null); Spacer(Modifier.width(8.dp))
        Text(if (showForm) "Ocultar formulario" else "Añadir semana")
    }

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
                    viewModel.agregarSemana(cursoId, nombreSemana.trim())
                    nombreSemana = ""
                    showForm = false
                    onSnack("Semana agregada")
                }
            }) { Text("Guardar") }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        semanas.forEach { semana ->
            SemanaItemSwipe(semana, viewModel, onSnack)
        }
    }
}

/* ===================== Semana (swipe-to-delete) ===================== */

@Composable
private fun SemanaItemSwipe(
    semana: SemanaEntity,
    viewModel: BibliotecaViewModel,
    onSnack: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var askConfirm by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { target ->
            when (target) {
                SwipeToDismissBoxValue.EndToStart -> {
                    askConfirm = true
                    false
                }
                else -> false
            }
        }
    )

    ConfirmDeleteDialog(
        open = askConfirm,
        title = "Eliminar semana",
        message = "¿Seguro que quieres eliminar \"${semana.titulo}\" y sus materiales?",
        onConfirm = {
            viewModel.eliminarSemanaById(semana.id) { ok ->
                onSnack(if (ok) "Semana eliminada" else "No se pudo eliminar")
            }
        },
        onDismiss = { askConfirm = false }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val bg = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceVariant
            Box(
                Modifier.fillMaxSize().background(bg).padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) { Icon(Icons.Outlined.Delete, null) }
        },
        content = {
            FlatCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CalendarViewWeek, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        semana.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f).clickable { expanded = !expanded },
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            null
                        )
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        MaterialesList(
                            semanaId = semana.id,
                            viewModel = viewModel,
                            onSnack = onSnack
                        )
                    }
                }
            }
        }
    )
}


/* ===================== Materiales (swipe-to-delete) ===================== */

@Composable
private fun MaterialesList(
    semanaId: Int,
    viewModel: BibliotecaViewModel,
    onSnack: (String) -> Unit
) {
    val context = LocalContext.current
    val material by viewModel.observeMaterialBySemana(semanaId).collectAsState(initial = null)

    var showDialog by remember { mutableStateOf(false) }

    FilledTonalButton(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Outlined.Add, null); Spacer(Modifier.width(8.dp))
        Text("Agregar material")
    }

    // Diálogo único para agregar info + varios docs/imgs/enlaces
    if (showDialog) {
        CrearMaterialDialog(
            onDismiss = { showDialog = false },
            onSave = { info, docs, imgs, links ->
                viewModel.guardarMaterialDesdeDialogo(
                    semanaId = semanaId,
                    info = info,
                    docs = docs,
                    imgs = imgs,
                    links = links,
                    onDone = {
                        onSnack("Material guardado")
                        showDialog = false
                    },
                    onError = { msg -> onSnack(msg) }
                )
            }
        )
    }

    Spacer(Modifier.height(8.dp))

    if (material == null) {
        Text("Sin materiales.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        MaterialSectionSingle(material = material!!, viewModel = viewModel, onSnack = onSnack, context = context)
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CrearMaterialDialog(
    onDismiss: () -> Unit,
    onSave: (info: String?, docs: List<String>, imgs: List<String>, links: List<String>) -> Unit
) {
    var info by remember { mutableStateOf("") }

    val docs = remember { mutableStateListOf<String>() }
    val imgs = remember { mutableStateListOf<String>() }
    val links = remember { mutableStateListOf<String>() }
    var linkText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // pickers múltiples
    val docPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris -> docs.addAll(uris.map { it.toString() }) }

    val imgPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> imgs.addAll(uris.map { it.toString() }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar material") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = info,
                    onValueChange = { info = it },
                    label = { Text("Información (opcional, única)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Documentos:", style = MaterialTheme.typography.titleSmall)
                OutlinedButton(onClick = { docPicker.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.AttachFile, null); Spacer(Modifier.width(8.dp)); Text("Añadir documentos")
                }
                if (docs.isEmpty()) {
                    Text("— Ningún documento —", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        docs.forEach { s ->
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text(Uri.parse(s).lastPathSegment ?: "doc") },
                                trailingIcon = { IconButton(onClick = { docs.remove(s) }) { Icon(Icons.Outlined.Close, null) } }
                            )
                        }
                    }
                }

                Text("Imágenes:", style = MaterialTheme.typography.titleSmall)
                OutlinedButton(onClick = { imgPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Image, null); Spacer(Modifier.width(8.dp)); Text("Añadir imágenes")
                }
                if (imgs.isEmpty()) {
                    Text("— Ninguna imagen —", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        imgs.forEach { s ->
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text(Uri.parse(s).lastPathSegment ?: "img") },
                                trailingIcon = { IconButton(onClick = { imgs.remove(s) }) { Icon(Icons.Outlined.Close, null) } }
                            )
                        }
                    }
                }

                Text("Enlaces:", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = linkText,
                        onValueChange = { linkText = it },
                        label = { Text("URL (https://...)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(onClick = {
                        val u = linkText.trim()
                        if (u.isNotBlank()) { links.add(u); linkText = "" }
                    }) { Text("Añadir") }
                }
                if (links.isEmpty()) {
                    Text("— Ningún enlace —", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        links.forEach { u ->
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text(u, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                trailingIcon = { IconButton(onClick = { links.remove(u) }) { Icon(Icons.Outlined.Close, null) } }
                            )
                        }
                    }
                }

                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val empty = info.isBlank() && docs.isEmpty() && imgs.isEmpty() && links.isEmpty()
                if (empty) { error = "Agrega información, documento, imagen o enlace"; return@TextButton }
                onSave(info.ifBlank { null }, docs.toList(), imgs.toList(), links.toList())
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

/* ===================== Material (swipe-to-delete) ===================== */

@Composable
private fun SwipeItemRow(
    state: SwipeToDismissBoxState,
    leading: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit
) {
    // Mostrar ícono SOLO mientras se está swippeando o quedó en EndToStart (antes de reset)
    val showBg = state.dismissDirection != null ||
            state.currentValue == SwipeToDismissBoxValue.EndToStart ||
            state.targetValue == SwipeToDismissBoxValue.EndToStart

    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            if (showBg) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) }
            } else {
                Box(Modifier.fillMaxSize())
            }
        },
        content = {
            // Cubrimos el contenido con el mismo fondo para que nunca “asome” el background
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onClick() }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leading()
                    Spacer(Modifier.width(8.dp))
                    text()
                }
            }
        }
    )
}

@Composable
private fun ConfirmDeleteDialog(
    open: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!open) return
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) { Text("Eliminar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(title) },
        text = { Text(message) }
    )
}
@Composable
private fun MaterialSectionSingle(
    material: MaterialEntity,
    viewModel: BibliotecaViewModel,
    onSnack: (String) -> Unit,
    context: Context
) {
    val docs  by viewModel.getDocs(material.id).collectAsState(initial = emptyList())
    val imgs  by viewModel.getImgs(material.id).collectAsState(initial = emptyList())
    val links by viewModel.getLinks(material.id).collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    // ==== Estados de confirmación + swipe pendiente por reset ====
    var askClearInfo by remember { mutableStateOf(false) }
    var infoPendingState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    var askDeleteDocId by remember { mutableStateOf<Int?>(null) }
    var docPendingState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    var askDeleteImgId by remember { mutableStateOf<Int?>(null) }
    var imgPendingState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    var askDeleteLinkId by remember { mutableStateOf<Int?>(null) }
    var linkPendingState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    fun reset(state: SwipeToDismissBoxState?) = scope.launch {
        state?.snapTo(SwipeToDismissBoxValue.Settled)
    }

    FlatCard {
        // ===== Información (única, swipe para borrar con confirmación) =====
        Text("Información:", style = MaterialTheme.typography.titleSmall)
        if (material.info.isNullOrBlank()) {
            Text("— Sin información —", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            val dismissInfo = rememberSwipeToDismissBoxState()
            // si llega a EndToStart → abrir diálogo y recordar este state para resetear
            LaunchedEffect(dismissInfo.currentValue) {
                if (dismissInfo.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    infoPendingState = dismissInfo
                    askClearInfo = true
                }
            }

            // Fondo visible solo durante swipe
            val showBgInfo = dismissInfo.dismissDirection != null ||
                    dismissInfo.currentValue == SwipeToDismissBoxValue.EndToStart ||
                    dismissInfo.targetValue == SwipeToDismissBoxValue.EndToStart

            SwipeToDismissBox(
                state = dismissInfo,
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    if (showBgInfo) {
                        Box(
                            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    } else Box(Modifier.fillMaxSize())
                },
                content = {
                    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                        Text(
                            text = material.info ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }
                }
            )
        }

        Spacer(Modifier.height(12.dp)); Divider(modifier = Modifier.alpha(0.15f)); Spacer(Modifier.height(12.dp))

        // ===== Documentos (múltiples) =====
        Text("Documentos:", style = MaterialTheme.typography.titleSmall)
        if (docs.isEmpty()) {
            Text("— Ningún documento —", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                docs.forEach { d ->
                    val dismiss = rememberSwipeToDismissBoxState()
                    // Al llegar a EndToStart, abrimos confirm y guardamos el state para reset
                    LaunchedEffect(dismiss.currentValue) {
                        if (dismiss.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            docPendingState = dismiss
                            askDeleteDocId = d.id
                        }
                    }
                    SwipeItemRow(
                        state = dismiss,
                        leading = { Icon(Icons.Outlined.InsertDriveFile, null) },
                        text = {
                            Text(
                                d.name ?: Uri.parse(d.uriDoc).lastPathSegment ?: "Documento",
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { abrirArchivo(context, Uri.parse(d.uriDoc), "application/*") }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp)); Divider(modifier = Modifier.alpha(0.15f)); Spacer(Modifier.height(12.dp))

        // ===== Imágenes (múltiples) =====
        Text("Imágenes:", style = MaterialTheme.typography.titleSmall)
        if (imgs.isEmpty()) {
            Text("— Ninguna imagen —", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                imgs.forEach { img ->
                    val dismiss = rememberSwipeToDismissBoxState()
                    LaunchedEffect(dismiss.currentValue) {
                        if (dismiss.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            imgPendingState = dismiss
                            askDeleteImgId = img.id
                        }
                    }
                    SwipeItemRow(
                        state = dismiss,
                        leading = { Icon(Icons.Outlined.Image, null) },
                        text = {
                            Text(
                                Uri.parse(img.uriImg).lastPathSegment ?: "Imagen",
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = { abrirArchivo(context, Uri.parse(img.uriImg), "image/*") }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp)); Divider(modifier = Modifier.alpha(0.15f)); Spacer(Modifier.height(12.dp))

        // ===== Enlaces (múltiples) =====
        Text("Enlaces:", style = MaterialTheme.typography.titleSmall)
        if (links.isEmpty()) {
            Text("— Ningún enlace —", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                links.forEach { l ->
                    val dismiss = rememberSwipeToDismissBoxState()
                    LaunchedEffect(dismiss.currentValue) {
                        if (dismiss.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            linkPendingState = dismiss
                            askDeleteLinkId = l.id
                        }
                    }
                    SwipeItemRow(
                        state = dismiss,
                        leading = { Icon(Icons.Outlined.Link, null) },
                        text = {
                            Text(
                                l.url,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(l.url))) }
                            catch (_: Exception) {
                                Toast.makeText(context, "No se puede abrir el enlace", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    // ===== Diálogos de confirmación (reseteando swipe SIEMPRE) =====

    ConfirmDeleteDialog(
        open = askClearInfo,
        title = "Eliminar información",
        message = "¿Seguro que quieres eliminar el texto de información?",
        onConfirm = {
            viewModel.clearInfo(material.id)
            onSnack("Información eliminada")
            reset(infoPendingState)
            askClearInfo = false
            infoPendingState = null
        },
        onDismiss = {
            reset(infoPendingState)
            askClearInfo = false
            infoPendingState = null
        }
    )

    ConfirmDeleteDialog(
        open = askDeleteDocId != null,
        title = "Eliminar documento",
        message = "¿Seguro que quieres eliminar este documento?",
        onConfirm = {
            askDeleteDocId?.let { viewModel.deleteDocById(it); onSnack("Documento eliminado") }
            reset(docPendingState)
            askDeleteDocId = null
            docPendingState = null
        },
        onDismiss = {
            reset(docPendingState)
            askDeleteDocId = null
            docPendingState = null
        }
    )

    ConfirmDeleteDialog(
        open = askDeleteImgId != null,
        title = "Eliminar imagen",
        message = "¿Seguro que quieres eliminar esta imagen?",
        onConfirm = {
            askDeleteImgId?.let { viewModel.deleteImgById(it); onSnack("Imagen eliminada") }
            reset(imgPendingState)
            askDeleteImgId = null
            imgPendingState = null
        },
        onDismiss = {
            reset(imgPendingState)
            askDeleteImgId = null
            imgPendingState = null
        }
    )

    ConfirmDeleteDialog(
        open = askDeleteLinkId != null,
        title = "Eliminar enlace",
        message = "¿Seguro que quieres eliminar este enlace?",
        onConfirm = {
            askDeleteLinkId?.let { viewModel.deleteLinkById(it); onSnack("Enlace eliminado") }
            reset(linkPendingState)
            askDeleteLinkId = null
            linkPendingState = null
        },
        onDismiss = {
            reset(linkPendingState)
            askDeleteLinkId = null
            linkPendingState = null
        }
    )
}

/* ===================== Helper abrirArchivo ===================== */

private fun abrirArchivo(context: Context, uri: Uri, tipo: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, tipo)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show()
    }
}