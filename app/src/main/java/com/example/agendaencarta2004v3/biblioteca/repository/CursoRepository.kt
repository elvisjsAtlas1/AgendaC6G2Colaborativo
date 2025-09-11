package com.example.agendaencarta2004v3.biblioteca.repository

import com.example.agendaencarta2004v3.biblioteca.dao.CursoDao
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
class CursoRepository(private val cursoDao: CursoDao) {

    fun getAllCursos(): Flow<List<CursoEntity>> {
        return cursoDao.getAllCursos()
    }
    // NUEVO: snapshot inmediato (útil para parsers, workers, etc.)
    suspend fun getAllCursosOnce(): List<CursoEntity> =
        cursoDao.getAllCursos().first()

    suspend fun insertCurso(curso: CursoEntity) {
        cursoDao.insertCurso(curso)
    }
}