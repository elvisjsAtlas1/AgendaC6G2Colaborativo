package com.example.agendaencarta2004v3.biblioteca.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import com.example.agendaencarta2004v3.biblioteca.entity.SemanaEntity
import com.example.agendaencarta2004v3.biblioteca.repository.CursoRepository
import com.example.agendaencarta2004v3.biblioteca.repository.MaterialRepository
import com.example.agendaencarta2004v3.biblioteca.repository.SemanaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BibliotecaViewModel(
    private val cursoRepository: CursoRepository,
    private val semanaRepository: SemanaRepository,
    private val materialRepository: MaterialRepository
) : ViewModel() {

    // 🔹 Cursos expuestos como StateFlow
    val cursos: StateFlow<List<CursoEntity>> =
        cursoRepository.getAllCursos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------------
    // CURSOS
    // ------------------------
    fun agregarCurso(nombre: String) {
        viewModelScope.launch {
            val curso = CursoEntity(nombre = nombre)
            cursoRepository.insertCurso(curso)
        }
    }

    // ------------------------
    // SEMANAS
    // ------------------------
    fun agregarSemana(cursoId: Int, titulo: String) {
        viewModelScope.launch {
            val semana = SemanaEntity(cursoId = cursoId, titulo = titulo)
            semanaRepository.insertSemana(semana)
        }
    }

    // 🔹 OBTENER semanas de un curso
    fun getSemanasByCurso(cursoId: Int): Flow<List<SemanaEntity>> {
        return semanaRepository.getSemanasByCurso(cursoId)
    }

    // ------------------------
    // MATERIALES
    // ------------------------
    fun agregarMaterial(
        semanaId: Int,
        info: String,
        uriDoc: String? = null,
        uriImg: String? = null,
        url: String? = null
    ) {
        viewModelScope.launch {
            materialRepository.agregarMaterial(semanaId, info, uriDoc, uriImg, url)
        }
    }

    // 🔹 OBTENER materiales de una semana
    fun getMaterialesBySemana(semanaId: Int): Flow<List<MaterialEntity>> {
        return materialRepository.getMaterialesBySemana(semanaId)
    }
}
