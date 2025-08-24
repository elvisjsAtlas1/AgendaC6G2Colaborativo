package com.example.agendaencarta2004v3.biblioteca.ui

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
fun BibliotecaScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(
                text = "Biblioteca de Cursos",
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(listOf("Curso 1", "Curso 2")) { curso ->
            Text("- $curso")
        }
    }
}
