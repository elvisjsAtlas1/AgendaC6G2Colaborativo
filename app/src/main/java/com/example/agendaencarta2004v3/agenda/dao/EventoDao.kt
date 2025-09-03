package com.example.agendaencarta2004v3.agenda.dao

import androidx.room.*
import com.example.agendaencarta2004v3.agenda.entity.EventoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    // Crear
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evento: EventoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(eventos: List<EventoEntity>): List<Long>

    // Actualizar
    @Update
    suspend fun update(evento: EventoEntity)

    // Eliminar
    @Delete
    suspend fun delete(evento: EventoEntity)

    // Consultas
    @Query("SELECT * FROM eventos ORDER BY dia, horaInicio")
    fun getAll(): Flow<List<EventoEntity>>

    @Query("SELECT * FROM eventos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): EventoEntity?

    @Query("SELECT * FROM eventos WHERE cursoId = :cursoId ORDER BY dia, horaInicio")
    fun getByCursoId(cursoId: Int): Flow<List<EventoEntity>>

    @Query("SELECT * FROM eventos WHERE dia = :dia ORDER BY horaInicio")
    fun getByDia(dia: String): Flow<List<EventoEntity>>
}