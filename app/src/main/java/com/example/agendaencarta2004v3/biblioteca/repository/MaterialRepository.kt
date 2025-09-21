package com.example.agendaencarta2004v3.biblioteca.repository

import com.example.agendaencarta2004v3.biblioteca.dao.MaterialDao
import com.example.agendaencarta2004v3.biblioteca.dao.MaterialDocDao
import com.example.agendaencarta2004v3.biblioteca.dao.MaterialImgDao
import com.example.agendaencarta2004v3.biblioteca.dao.MaterialLinkDao
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialDocEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialImgEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialLinkEntity
import kotlinx.coroutines.flow.Flow

class MaterialRepository(
    private val materialDao: MaterialDao,
    private val docDao: MaterialDocDao,
    private val imgDao: MaterialImgDao,
    private val linkDao: MaterialLinkDao
) {

    /* =========================
       Material (padre)
       ========================= */

    suspend fun getLastMaterialBySemana(semanaId: Int): MaterialEntity? =
        materialDao.getLastBySemana(semanaId)

    suspend fun createMaterialAndReturnId(semanaId: Int, info: String?): Int =
        materialDao.insertMaterialReturnId(MaterialEntity(semanaId = semanaId, info = info)).toInt()


    suspend fun setInfo(materialId: Int, texto: String) {
        materialDao.setInfo(materialId, texto)
    }

    suspend fun clearInfo(materialId: Int) {
        materialDao.clearInfo(materialId)
    }

    // Eliminar material completo (sus hijos caen por CASCADE)
    suspend fun deleteMaterialById(materialId: Int): Int =
        materialDao.deleteMaterialById(materialId)

    suspend fun deleteMaterialesBySemanaId(semanaId: Int): Int =
        materialDao.deleteMaterialesBySemanaId(semanaId)


    suspend fun insertAndReturnId(material: MaterialEntity): Long =
        materialDao.insertMaterialReturnId(material)

    /* =========================
       Documentos (hijos)
       ========================= */

    fun getDocs(materialId: Int): Flow<List<MaterialDocEntity>> =
        docDao.getDocs(materialId)

    suspend fun addDoc(materialId: Int, uri: String, name: String? = null) {
        docDao.insert(MaterialDocEntity(materialId = materialId, uriDoc = uri, name = name))
    }

    suspend fun deleteDocById(docId: Int): Int =
        docDao.deleteById(docId)


    /* =========================
       Imágenes (hijos)
       ========================= */

    fun getImgs(materialId: Int): Flow<List<MaterialImgEntity>> =
        imgDao.getImgs(materialId)

    suspend fun addImg(materialId: Int, uri: String) {
        imgDao.insert(MaterialImgEntity(materialId = materialId, uriImg = uri))
    }

    suspend fun deleteImgById(imgId: Int): Int =
        imgDao.deleteById(imgId)


    /* =========================
       Enlaces (hijos)
       ========================= */

    fun getLinks(materialId: Int): Flow<List<MaterialLinkEntity>> =
        linkDao.getLinks(materialId)

    suspend fun addLink(materialId: Int, url: String) {
        linkDao.insert(MaterialLinkEntity(materialId = materialId, url = url))
    }

    suspend fun deleteLinkById(linkId: Int): Int =
        linkDao.deleteById(linkId)

    /** Obtiene el único material de la semana; si no existe, lo crea.
     *  - Si initialInfo no es nula y el material no tiene info, la establece.
     *  - Si el material ya tenía info, NO la sobreescribe (el usuario debe borrar primero). */
    suspend fun getOrCreateMaterialIdForSemana(
        semanaId: Int,
        initialInfo: String?
    ): Int {
        val existing = materialDao.getOneBySemana(semanaId)
        return if (existing != null) {
            if (!initialInfo.isNullOrBlank() && (existing.info.isNullOrBlank())) {
                materialDao.setInfo(existing.id, initialInfo.trim())
            }
            existing.id
        } else {
            materialDao.insertMaterialReturnId(
                MaterialEntity(semanaId = semanaId, info = initialInfo?.trim())
            ).toInt()
        }
    }

    // (Opcional) Exponer el flow del único material por semana
    fun observeMaterialBySemana(semanaId: Int): Flow<MaterialEntity?> =
        materialDao.observeOneBySemana(semanaId)
}
