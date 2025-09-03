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
            .onEach { lista ->
                Log.d("ActividadVM", "Lista actual de actividades: $lista")
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    init {
        viewModelScope.launch {
            repo.getAllActividades()
                .onEach { Log.d("ActividadVM", "Lista actual: $it") }
                .collect() // <- importante, sin collect no se ejecuta
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
            Log.d("ActividadVM", "➕ Insertando actividad: $nueva")
            repo.insertActividad(nueva)
        }
    }

    fun toggleActividad(actividad: ActividadEntity, hecho: Boolean) {
        viewModelScope.launch {
            val actualizada = actividad.copy(hecho = hecho)
            Log.d("ActividadVM", "✅ Cambiando estado: $actualizada")
            repo.updateActividad(actualizada)
        }
    }
}
