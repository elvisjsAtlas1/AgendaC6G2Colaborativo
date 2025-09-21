package com.example.agendaencarta2004v3.biblioteca.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [Index("semanaId")]
)
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val semanaId: Int,
    val info: String? = null // <- única, opcional
)

// Hijos (múltiples por material)
@Entity(
    tableName = "material_docs",
    foreignKeys = [ForeignKey(entity = MaterialEntity::class, parentColumns = ["id"], childColumns = ["materialId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("materialId")]
)
data class MaterialDocEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materialId: Int,
    val uriDoc: String,
    val name: String? = null
)

@Entity(
    tableName = "material_imgs",
    foreignKeys = [ForeignKey(entity = MaterialEntity::class, parentColumns = ["id"], childColumns = ["materialId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("materialId")]
)
data class MaterialImgEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materialId: Int,
    val uriImg: String
)

@Entity(
    tableName = "material_links",
    foreignKeys = [ForeignKey(entity = MaterialEntity::class, parentColumns = ["id"], childColumns = ["materialId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("materialId")]
)
data class MaterialLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materialId: Int,
    val url: String
)