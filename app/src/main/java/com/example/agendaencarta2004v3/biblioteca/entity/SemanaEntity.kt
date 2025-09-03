package com.example.agendaencarta2004v3.biblioteca.entity



import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "semanas",
    foreignKeys = [
        ForeignKey(
            entity = CursoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cursoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SemanaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cursoId: Int,
    val titulo: String
)