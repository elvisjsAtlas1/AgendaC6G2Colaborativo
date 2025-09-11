package com.example.agendaencarta2004v3.inicio.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.agendaencarta2004v3.R
import com.example.agendaencarta2004v3.actividades.viewmodel.ActividadViewModel
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    actividadViewModel: ActividadViewModel
) {
    // --- Launcher para Speech-to-Text ---
    val sttLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.getOrNull(0)
                ?.trim()
                .orEmpty()
            if (spokenText.isNotEmpty()) {
                actividadViewModel.handleVoiceCommand(spokenText)
            }
        }
    }

    // --- UI principal con AppBar y FAB ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
            )
        },
        floatingActionButton = {
            // Botón de micrófono con gate de permisos
            RecordAudioPermissionGate(
                onGranted = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Di la actividad que quieres agregar")
                    }
                    sttLauncher.launch(intent)
                }
            ) { request ->
                FloatingActionButton(onClick = { request() }) {
                    Icon(Icons.Outlined.Mic, contentDescription = "Hablar")
                }
            }
        }
    ) { inner ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // Contenido central
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.starcraftii),
                    contentDescription = "Imagen IA",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 24.dp)
                )
                Text(
                    text = "Bienvenido a la App",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Toca el micrófono para dictar una actividad.\n" +
                            "Ej.: “agrega tarea de matemáticas para mañana a las 5 pm”.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Feedback del último texto reconocido
                val ultimo by actividadViewModel.ultimoTextoReconocido.collectAsState()
                if (ultimo.isNotEmpty()) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("IA reconoció:", style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(6.dp))
                            Text(ultimo, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecordAudioPermissionGate(
    onGranted: () -> Unit,
    content: @Composable (requestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val permission = android.Manifest.permission.RECORD_AUDIO
    val hasPermission = remember { mutableStateOf(checkSelfPermission(context, permission)) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission.value = granted
        if (granted) onGranted() else {
            // (opcional) Toast/feedback
            Toast.makeText(context, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val request: () -> Unit = {
        if (hasPermission.value) onGranted() else launcher.launch(permission)
    }

    content(request)
}

private fun checkSelfPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}