package com.example.agendaencarta2004v3.actividades.repository

import android.util.Log
import com.example.agendaencarta2004v3.actividades.dao.ActividadDao
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import kotlinx.coroutines.flow.Flow

class ActividadRepository(private val dao: ActividadDao) {
    fun getAllActividades(): Flow<List<ActividadEntity>> = dao.getAllActividades()

    suspend fun insertActividad(actividad: ActividadEntity) {
        Log.d("ActividadRepository", "Insertando actividad: $actividad")
        dao.insertActividad(actividad)
    }

    suspend fun updateActividad(actividad: ActividadEntity) {
        Log.d("ActividadRepository", "Actualizando actividad: $actividad")
        dao.updateActividad(actividad)
    }
    suspend fun deleteActividad(actividad: ActividadEntity) = dao.deleteActividad(actividad)
}