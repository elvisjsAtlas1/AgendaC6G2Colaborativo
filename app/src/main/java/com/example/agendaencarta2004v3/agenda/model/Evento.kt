package com.example.agendaencarta2004v3.agenda.model

data class Evento(
    val cursoId: Int,
    val dia: String,
    val horaInicio: String,
    val horaFin: String,
    val aula: String
)