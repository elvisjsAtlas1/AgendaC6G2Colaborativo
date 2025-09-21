package com.example.agendaencarta2004v3.biblioteca.repository

import com.example.agendaencarta2004v3.biblioteca.dao.SemanaDao
import com.example.agendaencarta2004v3.biblioteca.entity.SemanaEntity
import kotlinx.coroutines.flow.Flow


class SemanaRepository(private val semanaDao: SemanaDao) {

    fun getSemanasByCurso(cursoId: Int): Flow<List<SemanaEntity>> {
        return semanaDao.getSemanasByCurso(cursoId)
    }

    suspend fun insertSemana(semana: SemanaEntity) {
        semanaDao.insertSemana(semana)
    }

    // --- ELIMINAR ---
    suspend fun deleteSemana(semana: SemanaEntity): Int =
        semanaDao.deleteSemana(semana)

    suspend fun deleteSemanaById(semanaId: Int): Int =
        semanaDao.deleteSemanaById(semanaId)

    // Opcional
    suspend fun deleteSemanasByCursoId(cursoId: Int): Int =
        semanaDao.deleteSemanasByCursoId(cursoId)
}