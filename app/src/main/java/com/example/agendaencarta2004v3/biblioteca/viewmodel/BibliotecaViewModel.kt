package com.example.agendaencarta2004v3.biblioteca.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialDocEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialImgEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialLinkEntity
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

    /* =========================
       CURSOS
       ========================= */

    val cursos: StateFlow<List<CursoEntity>> =
        cursoRepository.getAllCursos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarCurso(nombre: String) {
        viewModelScope.launch {
            cursoRepository.insertCurso(CursoEntity(nombre = nombre))
        }
    }

    fun eliminarCursoById(cursoId: Int, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val rows = cursoRepository.deleteCursoById(cursoId)
            onResult(rows > 0)
        }
    }

    /* =========================
       SEMANAS
       ========================= */

    fun agregarSemana(cursoId: Int, titulo: String) {
        viewModelScope.launch {
            semanaRepository.insertSemana(SemanaEntity(cursoId = cursoId, titulo = titulo))
        }
    }

    fun eliminarSemanaById(semanaId: Int, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val rows = semanaRepository.deleteSemanaById(semanaId)
            onResult(rows > 0)
        }
    }

    fun getSemanasByCurso(cursoId: Int): Flow<List<SemanaEntity>> =
        semanaRepository.getSemanasByCurso(cursoId)

    /* =========================
       MATERIALES (UN ÚNICO POR SEMANA)
       ========================= */

    // Si necesitas observar el único material de la semana (puede ser null si aún no existe)
    fun observeMaterialBySemana(semanaId: Int): Flow<MaterialEntity?> =
        materialRepository.observeMaterialBySemana(semanaId)

    /**
     * Guarda lo del diálogo "Agregar material" en el ÚNICO material de la semana.
     * - Crea el material si no existe.
     * - 'info' solo se establece si el material aún no tenía info (para cambiarla, primero bórrala).
     * - Agrega docs/imgs/links a las listas existentes.
     */
    fun guardarMaterialDesdeDialogo(
        semanaId: Int,
        info: String?,
        docs: List<String>,
        imgs: List<String>,
        links: List<String>,
        onDone: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val hasSomething =
                (!info.isNullOrBlank()) || docs.isNotEmpty() || imgs.isNotEmpty() || links.isNotEmpty()
            if (!hasSomething) { onError("Agrega información, documento, imagen o enlace."); return@launch }

            // ÚNICO material por semana: lo obtiene o lo crea; setea info solo si estaba vacía
            val materialId = materialRepository.getOrCreateMaterialIdForSemana(
                semanaId = semanaId,
                initialInfo = info
            )

            docs.forEach { materialRepository.addDoc(materialId, it) }
            imgs.forEach { materialRepository.addImg(materialId, it) }
            links.forEach { materialRepository.addLink(materialId, it) }

            onDone()
        }
    }

    /* ======= Info única (set/clear) y listas (get/add/delete) ======= */

    fun setInfo(materialId: Int, texto: String) {
        viewModelScope.launch { materialRepository.setInfo(materialId, texto) }
    }

    fun clearInfo(materialId: Int) {
        viewModelScope.launch { materialRepository.clearInfo(materialId) }
    }

    // Documentos
    fun getDocs(materialId: Int): Flow<List<MaterialDocEntity>> =
        materialRepository.getDocs(materialId)

    fun addDoc(materialId: Int, uri: String, name: String? = null) {
        viewModelScope.launch { materialRepository.addDoc(materialId, uri, name) }
    }

    fun deleteDocById(docId: Int) {
        viewModelScope.launch { materialRepository.deleteDocById(docId) }
    }

    // Imágenes
    fun getImgs(materialId: Int): Flow<List<MaterialImgEntity>> =
        materialRepository.getImgs(materialId)

    fun addImg(materialId: Int, uri: String) {
        viewModelScope.launch { materialRepository.addImg(materialId, uri) }
    }

    fun deleteImgById(imgId: Int) {
        viewModelScope.launch { materialRepository.deleteImgById(imgId) }
    }

    // Enlaces
    fun getLinks(materialId: Int): Flow<List<MaterialLinkEntity>> =
        materialRepository.getLinks(materialId)

    fun addLink(materialId: Int, url: String) {
        viewModelScope.launch { materialRepository.addLink(materialId, url) }
    }

    fun deleteLinkById(linkId: Int) {
        viewModelScope.launch { materialRepository.deleteLinkById(linkId) }
    }
}
