package com.example.agendaencarta2004v3.actividades.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import com.example.agendaencarta2004v3.actividades.repository.ActividadRepository
import com.example.agendaencarta2004v3.biblioteca.repository.CursoRepository
import com.example.agendaencarta2004v3.core.voice.VoiceParserMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActividadViewModel(
    private val actividadRepo: ActividadRepository,
    private val cursoRepository: CursoRepository
) : ViewModel() {

    // --- FEEDBACK VOZ ---
    private val _ultimoTextoReconocido = MutableStateFlow("")
    val ultimoTextoReconocido: StateFlow<String> = _ultimoTextoReconocido

    // --- LISTA PARA UI ---
    val actividades: StateFlow<List<ActividadEntity>> =
        actividadRepo.getAllActividades()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // --- AGREGAR ---
    fun addActividad(descripcion: String, cursoId: Int, fechaEntregaMillis: Long) {
        viewModelScope.launch {
            actividadRepo.insertActividad(
                ActividadEntity(
                    descripcion = descripcion,
                    cursoId = cursoId,
                    fechaEntrega = fechaEntregaMillis,
                    hecho = false
                )
            )
        }
    }

    // --- MARCAR HECHO / DESHECHO ---
    fun toggleActividad(actividad: ActividadEntity, hecho: Boolean) {
        viewModelScope.launch {
            actividadRepo.updateActividad(actividad.copy(hecho = hecho))
        }
    }

    // --- ELIMINAR (usado por el swipe) ---
    fun deleteActividad(actividad: ActividadEntity) {
        viewModelScope.launch {
            actividadRepo.deleteActividad(actividad)
        }
    }

    // --- VOZ ---
    fun handleVoiceCommand(spokenText: String) {
        viewModelScope.launch {
            _ultimoTextoReconocido.value = "Procesando: $spokenText"

            val cursosBD = cursoRepository.getAllCursosOnce()
                .map { VoiceParserMillis.CursoRow(id = it.id, nombre = it.nombre) }

            val parsed = VoiceParserMillis.parse(
                raw = spokenText,
                cursosBD = cursosBD,
                defaultHour = 23,
                defaultMinute = 59
            )

            val cursoId = parsed.cursoId
            val fechaMillis = parsed.fechaEntregaMillis

            if (cursoId != null && fechaMillis != null) {
                actividadRepo.insertActividad(
                    ActividadEntity(
                        descripcion = parsed.descripcion,
                        cursoId = cursoId,
                        fechaEntrega = fechaMillis,
                        hecho = false
                    )
                )

                val fechaTxt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(fechaMillis))
                val cursoNombre = cursosBD.firstOrNull { it.id == cursoId }?.nombre ?: "Curso"

                _ultimoTextoReconocido.value =
                    "✅ Guardado\nCurso: $cursoNombre\nFecha: $fechaTxt\nDesc: ${parsed.descripcion}"
            } else {
                val faltantes = buildList {
                    if (cursoId == null) add("curso")
                    if (fechaMillis == null) add("fecha")
                }.joinToString(" y ")
                _ultimoTextoReconocido.value =
                    "⚠️ No pude registrar por falta de $faltantes.\nDetectado: ${parsed.descripcion}"
            }
        }
    }
}
/*
// 🛠️ Código de depuración para ver la lista de actividades y operaciones en la base de datos
val actividades: StateFlow<List<ActividadEntity>> =
    repo.getAllActividades()
        .onEach { lista ->
            Log.d("ActividadVM", "Lista actual de actividades: $lista") // Mostrar lista cada vez que cambie
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

init {
    // 🛠️ Para depurar: recolecta el flujo y loguea cada actualización
    viewModelScope.launch {
        repo.getAllActividades()
            .onEach { Log.d("ActividadVM", "Lista actual: $it") } // Log alternativo de seguimiento
            .collect() // Importante: sin collect no se ejecuta el flujo
    }
}

fun addActividad(desc: String, cursoId: Int, fechaEntrega: Long) {
    viewModelScope.launch {
        val nueva = ActividadEntity(
            descripcion = desc,
            cursoId = cursoId,
            fechaEntrega = fechaEntrega,
            hecho = false
        )
        Log.d("ActividadVM", "➕ Insertando actividad: $nueva") // Log al agregar nueva actividad
        repo.insertActividad(nueva)
    }
}

fun toggleActividad(actividad: ActividadEntity, hecho: Boolean) {
    viewModelScope.launch {
        val actualizada = actividad.copy(hecho = hecho)
        Log.d("ActividadVM", "✅ Cambiando estado: $actualizada") // Log al cambiar estado de actividad
        repo.updateActividad(actualizada)
    }
}
*/