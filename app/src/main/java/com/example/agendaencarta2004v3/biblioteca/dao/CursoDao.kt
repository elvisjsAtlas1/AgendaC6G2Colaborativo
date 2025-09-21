package com.example.agendaencarta2004v3.biblioteca.dao

import androidx.room.*
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface CursoDao {

    @Query("SELECT * FROM cursos")
    fun getAllCursos(): Flow<List<CursoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurso(curso: CursoEntity)

    @Query("SELECT * FROM cursos WHERE nombre LIKE :q")
    suspend fun searchByName(q: String): List<CursoEntity>

    // --- ELIMINAR ---
    @Delete
    suspend fun deleteCurso(curso: CursoEntity): Int

    @Query("DELETE FROM cursos WHERE id = :cursoId")
    suspend fun deleteCursoById(cursoId: Int): Int

}