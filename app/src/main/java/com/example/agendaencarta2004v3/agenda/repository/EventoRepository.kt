package com.example.agendaencarta2004v3.agenda.repository

import com.example.agendaencarta2004v3.agenda.dao.EventoDao
import com.example.agendaencarta2004v3.agenda.entity.EventoEntity
import kotlinx.coroutines.flow.Flow

class EventoRepository(
    private val dao: EventoDao
) {
    fun getAll(): Flow<List<EventoEntity>> = dao.getAll()
    fun getByCursoId(cursoId: Int): Flow<List<EventoEntity>> = dao.getByCursoId(cursoId)
    fun getByDia(dia: String): Flow<List<EventoEntity>> = dao.getByDia(dia)

    suspend fun getById(id: Int): EventoEntity? = dao.getById(id)

    suspend fun insert(evento: EventoEntity): Long = dao.insert(evento)
    suspend fun insertAll(eventos: List<EventoEntity>): List<Long> = dao.insertAll(eventos)

    suspend fun update(evento: EventoEntity) = dao.update(evento)
    suspend fun delete(evento: EventoEntity) = dao.delete(evento)
}