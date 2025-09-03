package com.example.agendaencarta2004v3.biblioteca.repository

import com.example.agendaencarta2004v3.biblioteca.dao.CursoDao
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import kotlinx.coroutines.flow.Flow

class CursoRepository(private val cursoDao: CursoDao) {

    fun getAllCursos(): Flow<List<CursoEntity>> {
        return cursoDao.getAllCursos()
    }

    suspend fun insertCurso(curso: CursoEntity) {
        cursoDao.insertCurso(curso)
    }
}