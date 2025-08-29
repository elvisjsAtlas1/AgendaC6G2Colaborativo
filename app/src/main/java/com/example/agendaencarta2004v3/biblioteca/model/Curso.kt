package com.example.agendaencarta2004v3.biblioteca.model

// CursoModel.kt
data class Curso(
    val id: Int,
    var nombre: String,
    val semanas: MutableList<Semana> = mutableListOf()
)

data class Semana(
    val id: Int,
    var titulo: String,
    val materiales: MutableList<Material> = mutableListOf()
)

sealed class Material {
    data class Documento(val id: Int, val titulo: String, val uri: String) : Material()
    data class Imagen(val id: Int, val titulo: String, val uri: String) : Material()
    data class Enlace(val id: Int, val titulo: String, val url: String) : Material()
}
