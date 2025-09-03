package com.example.agendaencarta2004v3.biblioteca.dao

import androidx.room.*
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {

    @Query("SELECT * FROM materiales WHERE semanaId = :semanaId")
    fun getMaterialesBySemana(semanaId: Int): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)
}