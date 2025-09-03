package com.example.agendaencarta2004v3.agenda.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eventos")
data class EventoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cursoId: Int,
    val dia: String,
    val horaInicio: String,
    val horaFin: String,
    val aula: String
)