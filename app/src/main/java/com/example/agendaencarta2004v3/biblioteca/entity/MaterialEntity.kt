package com.example.agendaencarta2004v3.biblioteca.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "materiales",
    foreignKeys = [
        ForeignKey(
            entity = SemanaEntity::class,
            parentColumns = ["id"],
            childColumns = ["semanaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val semanaId: Int,
    val titulo: String,
    val uri: String? = null,
    val url: String? = null,
    val tipo: String // "DOCUMENTO", "IMAGEN", "ENLACE"
)