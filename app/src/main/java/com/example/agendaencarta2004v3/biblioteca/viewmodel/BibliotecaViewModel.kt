package com.example.agendaencarta2004v3.biblioteca.viewmodel

import androidx.compose.runtime.mutableStateListOf

import androidx.lifecycle.ViewModel
import com.example.agendaencarta2004v3.biblioteca.model.Curso
import com.example.agendaencarta2004v3.biblioteca.model.Material
import com.example.agendaencarta2004v3.biblioteca.model.Semana
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BibliotecaViewModel : ViewModel() {

    private val _cursos = mutableStateListOf<Curso>()
    val cursos: List<Curso> get() = _cursos

    fun agregarCurso(nombre: String) {
        val nuevoCurso = Curso(id = _cursos.size + 1, nombre = nombre)
        _cursos.add(nuevoCurso)
    }

    fun eliminarCurso(curso: Curso) {
        _cursos.remove(curso)
    }

    fun agregarSemana(curso: Curso, titulo: String) {
        val nuevaSemana = Semana(id = curso.semanas.size + 1, titulo = titulo)
        curso.semanas.add(nuevaSemana)
    }

    fun agregarMaterial(semana: Semana, material: Material) {
        semana.materiales.add(material)
    }
}

class CursoViewModel : ViewModel() {
    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    fun agregarCurso(nombre: String) {
        val nuevoCurso = Curso(
            id = (_cursos.value.size + 1), // ejemplo autoincremental
            nombre = nombre
        )
        _cursos.value = _cursos.value + nuevoCurso
    }
}

