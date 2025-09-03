package com.example.agendaencarta2004v3.actividades.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descripcion: String,
    val cursoId: Int, // Relación con CursoEntity
    val fechaEntrega: Long, // Guardamos como timestamp
    val hecho: Boolean = false // Estado por defecto
)