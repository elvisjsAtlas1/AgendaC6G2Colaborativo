package com.example.agendaencarta2004v3.actividades.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descripcion: String,
    val cursoId: Int,           // Relación con CursoEntity
    val fechaEntrega: Long,     // Timestamp FECHA+HORA
    val hecho: Boolean = false, // Estado por defecto
    // NUEVO: minutos de anticipación (null = sin recordatorio)
    val avisoMinAntes: Int? = null
)