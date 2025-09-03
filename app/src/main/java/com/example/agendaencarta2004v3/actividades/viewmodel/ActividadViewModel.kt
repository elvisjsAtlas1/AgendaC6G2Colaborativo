package com.example.agendaencarta2004v3.actividades.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import com.example.agendaencarta2004v3.actividades.repository.ActividadRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class ActividadViewModel(private val repo: ActividadRepository) : ViewModel() {

    val actividades: StateFlow<List<ActividadEntity>> =
        repo.getAllActividades()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addActividad(desc: String, cursoId: Int, fechaEntrega: Long) {
        viewModelScope.launch {
            repo.insertActividad(
                ActividadEntity(
                    descripcion = desc,
                    cursoId = cursoId,
                    fechaEntrega = fechaEntrega,
                    hecho = false
                )
            )
        }
    }

    fun toggleActividad(actividad: ActividadEntity, hecho: Boolean) {
        viewModelScope.launch {
            repo.updateActividad(actividad.copy(hecho = hecho))
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