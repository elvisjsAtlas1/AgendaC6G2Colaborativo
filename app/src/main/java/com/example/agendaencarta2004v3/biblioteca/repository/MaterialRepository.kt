package com.example.agendaencarta2004v3.biblioteca.repository

import com.example.agendaencarta2004v3.biblioteca.dao.MaterialDao
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

class MaterialRepository(private val materialDao: MaterialDao) {

    fun getMaterialesBySemana(semanaId: Int): Flow<List<MaterialEntity>> {
        return materialDao.getMaterialesBySemana(semanaId)
    }

    suspend fun insertMaterial(material: MaterialEntity) {
        materialDao.insertMaterial(material)
    }
}