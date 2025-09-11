package com.example.agendaencarta2004v3.agenda.repository

import com.example.agendaencarta2004v3.agenda.dao.EventoDao
import com.example.agendaencarta2004v3.agenda.entity.EventoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EventoRepository(
    private val dao: EventoDao
) {
    fun getAll(): Flow<List<EventoEntity>> = dao.getAll()

    suspend fun insert(evento: EventoEntity): Long = dao.insert(evento)

    suspend fun update(evento: EventoEntity) = dao.update(evento)

    suspend fun delete(evento: EventoEntity) = dao.delete(evento)

    //obtener snapshot una sola vez
    suspend fun getAllOnce(): List<EventoEntity> = dao.getAll().first()

    //upsert semántico
    suspend fun upsert(evento: EventoEntity): Long {
        return if (evento.id == 0) dao.insert(evento) else {
            dao.update(evento); evento.id.toLong()
        }
    }
}

