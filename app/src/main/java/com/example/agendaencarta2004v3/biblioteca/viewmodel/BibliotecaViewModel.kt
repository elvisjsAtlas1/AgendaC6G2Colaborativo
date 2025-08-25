package com.example.agendaencarta2004v3.biblioteca.viewmodel

import androidx.lifecycle.ViewModel
import com.example.agendaencarta2004v3.biblioteca.model.Curso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BibliotecaViewModel : ViewModel() {

    // Lista de cursos de ejemplo
    private val _cursos = MutableStateFlow<List<Curso>>(listOf(
        Curso(1, "Matemáticas"),
        Curso(2, "Historia"),
        Curso(3, "Biología"),
        Curso(4, "Programación Kotlin"),
        Curso(5, "Física")
    ))

    val cursos: StateFlow<List<Curso>> = _cursos

    // Función para agregar un curso nuevo
    fun agregarCurso(curso: Curso) {
        _cursos.value = _cursos.value + curso
    }
}
