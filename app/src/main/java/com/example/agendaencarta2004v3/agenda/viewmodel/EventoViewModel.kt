package com.example.agendaencarta2004v3.agenda.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaencarta2004v3.agenda.entity.EventoEntity
import com.example.agendaencarta2004v3.agenda.repository.EventoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventoViewModel(
    private val repository: EventoRepository
) : ViewModel() {

    // Flujo con todos los eventos
    val eventos: StateFlow<List<EventoEntity>> =
        repository.getAll()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // CRUD
    fun agregarEvento(cursoId: Int, dia: String, horaInicio: String, horaFin: String, aula: String) {
        val nuevoEvento = EventoEntity(
            cursoId = cursoId,
            dia = dia,
            horaInicio = horaInicio,
            horaFin = horaFin,
            aula = aula
        )
        viewModelScope.launch { repository.insert(nuevoEvento) }
    }

    fun insertAll(eventos: List<EventoEntity>) {
        viewModelScope.launch { repository.insertAll(eventos) }
    }

    fun update(evento: EventoEntity) {
        viewModelScope.launch { repository.update(evento) }
    }

    fun delete(evento: EventoEntity) {
        viewModelScope.launch { repository.delete(evento) }
    }

    // Consultas filtradas
    fun eventosPorCurso(cursoId: Int) = repository.getByCursoId(cursoId)
    fun eventosPorDia(dia: String) = repository.getByDia(dia)

    suspend fun obtenerPorId(id: Int) = repository.getById(id)
}