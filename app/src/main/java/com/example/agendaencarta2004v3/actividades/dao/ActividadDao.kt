package com.example.agendaencarta2004v3.actividades.dao

import androidx.room.*
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades")
    fun getAllActividades(): Flow<List<ActividadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividad(actividad: ActividadEntity)

    @Update
    suspend fun updateActividad(actividad: ActividadEntity)

    @Delete
    suspend fun deleteActividad(actividad: ActividadEntity)
}