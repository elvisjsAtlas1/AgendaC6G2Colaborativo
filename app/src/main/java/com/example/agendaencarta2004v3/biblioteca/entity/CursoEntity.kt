package com.example.agendaencarta2004v3.biblioteca.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cursos")
data class CursoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String
)