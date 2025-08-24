package com.example.agendaencarta2004v3.agenda.viewmodel

import androidx.lifecycle.ViewModel
import com.example.agendaencarta2004v3.agenda.model.Evento
import com.example.agendaencarta2004v3.biblioteca.model.Curso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AgendaViewModel : ViewModel() {
    private val _cursos = MutableStateFlow<List<com.example.agendaencarta2004v3.biblioteca.model.Curso>>(emptyList())
    val cursos: StateFlow<List<com.example.agendaencarta2004v3.biblioteca.model.Curso>> = _cursos


    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    fun agregarEvento(cursoId: Int, dia: String, horaInicio: String, horaFin: String, aula: String) {
        val nuevoEvento = Evento(cursoId, dia, horaInicio, horaFin, aula)
        _eventos.value = _eventos.value + nuevoEvento
    }
}