package com.example.agendaencarta2004v3.biblioteca.dao

import androidx.room.*
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialDocEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialImgEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialLinkEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    // existentes...
    @Query("SELECT * FROM materiales WHERE semanaId = :semanaId")
    fun getMaterialesBySemana(semanaId: Int): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMaterial(material: MaterialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialReturnId(material: MaterialEntity): Long


    @Query("UPDATE materiales SET info = :texto WHERE id = :materialId")
    suspend fun setInfo(materialId: Int, texto: String)

    @Query("UPDATE materiales SET info = NULL WHERE id = :materialId")
    suspend fun clearInfo(materialId: Int)

    @Query("DELETE FROM materiales WHERE id = :materialId")
    suspend fun deleteMaterialById(materialId: Int): Int

    // Borra TODOS los materiales de una semana
    @Query("DELETE FROM materiales WHERE semanaId = :semanaId")
    suspend fun deleteMaterialesBySemanaId(semanaId: Int): Int

    @Query("SELECT * FROM materiales WHERE semanaId = :semanaId ORDER BY id DESC LIMIT 1")
    suspend fun getLastBySemana(semanaId: Int): MaterialEntity?

    /** Devuelve el único material de la semana (si existe) */
    @Query("SELECT * FROM materiales WHERE semanaId = :semanaId LIMIT 1")
    suspend fun getOneBySemana(semanaId: Int): MaterialEntity?

    /** (Opcional) Para observar el único material de la semana */
    @Query("SELECT * FROM materiales WHERE semanaId = :semanaId LIMIT 1")
    fun observeOneBySemana(semanaId: Int): Flow<MaterialEntity?>


}

@Dao
interface MaterialDocDao {
    @Query("SELECT * FROM material_docs WHERE materialId = :materialId")
    fun getDocs(materialId: Int): Flow<List<MaterialDocEntity>>
    @Insert suspend fun insert(doc: MaterialDocEntity)
    @Query("DELETE FROM material_docs WHERE id = :docId") suspend fun deleteById(docId: Int): Int
}

@Dao
interface MaterialImgDao {
    @Query("SELECT * FROM material_imgs WHERE materialId = :materialId")
    fun getImgs(materialId: Int): Flow<List<MaterialImgEntity>>
    @Insert suspend fun insert(img: MaterialImgEntity)
    @Query("DELETE FROM material_imgs WHERE id = :imgId") suspend fun deleteById(imgId: Int): Int
}

@Dao
interface MaterialLinkDao {
    @Query("SELECT * FROM material_links WHERE materialId = :materialId")
    fun getLinks(materialId: Int): Flow<List<MaterialLinkEntity>>
    @Insert suspend fun insert(link: MaterialLinkEntity)
    @Query("DELETE FROM material_links WHERE id = :linkId") suspend fun deleteById(linkId: Int): Int
}