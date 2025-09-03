package com.example.agendaencarta2004v3.biblioteca.dao

import androidx.room.*
import com.example.agendaencarta2004v3.biblioteca.entity.SemanaEntity

import kotlinx.coroutines.flow.Flow


@Dao
interface SemanaDao {

    @Query("SELECT * FROM semanas WHERE cursoId = :cursoId")
    fun getSemanasByCurso(cursoId: Int): Flow<List<SemanaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemana(semana: SemanaEntity)
}