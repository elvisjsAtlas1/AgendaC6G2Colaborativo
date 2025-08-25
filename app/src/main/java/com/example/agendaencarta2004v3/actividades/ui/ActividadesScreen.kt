package com.example.agendaencarta2004v3.actividades.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActividadesScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Actividades Pendientes", style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            items(listOf("Actividad 1", "Actividad 2")) { actividad ->
                Text("• $actividad")
            }
        }
    }
}
