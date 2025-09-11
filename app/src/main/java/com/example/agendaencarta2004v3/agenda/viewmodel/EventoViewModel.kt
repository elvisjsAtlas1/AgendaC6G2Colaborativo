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

    val eventos: StateFlow<List<EventoEntity>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun agregarEvento(cursoId: Int, dia: String, horaInicio: String, horaFin: String, aula: String) {
        viewModelScope.launch {
            repository.insert(
                EventoEntity(
                    id = 0,
                    cursoId = cursoId,
                    dia = dia,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    aula = aula
                )
            )
        }
    }

    fun actualizarEvento(evento: EventoEntity) {
        viewModelScope.launch { repository.update(evento) }
    }

    fun eliminarEvento(evento: EventoEntity) {
        viewModelScope.launch { repository.delete(evento) }
    }
}
