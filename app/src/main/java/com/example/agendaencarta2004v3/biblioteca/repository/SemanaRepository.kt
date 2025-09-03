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
}