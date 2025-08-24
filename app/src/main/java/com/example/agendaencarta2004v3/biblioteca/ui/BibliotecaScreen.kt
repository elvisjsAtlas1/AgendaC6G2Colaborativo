package com.example.agendaencarta2004v3.biblioteca.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendaencarta2004v3.biblioteca.viewmodel.BibliotecaViewModel

@Composable
fun BibliotecaScreen(bibliotecaViewModel: BibliotecaViewModel = viewModel()) {
    // Obtenemos la lista de cursos como estado
    val cursos by bibliotecaViewModel.cursos.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(
                text = "Biblioteca de Cursos",
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(cursos) { curso ->
            Text("- ${curso.nombre}")
        }
    }
}
