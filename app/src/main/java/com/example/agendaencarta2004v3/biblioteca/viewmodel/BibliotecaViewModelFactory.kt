package com.example.agendaencarta2004v3.biblioteca.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaencarta2004v3.biblioteca.repository.CursoRepository
import com.example.agendaencarta2004v3.biblioteca.repository.MaterialRepository
import com.example.agendaencarta2004v3.biblioteca.repository.SemanaRepository
import com.example.agendaencarta2004v3.core.database.AppDatabase


class BibliotecaViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BibliotecaViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val cursoRepository = CursoRepository(db.cursoDao())
            val semanaRepository = SemanaRepository(db.semanaDao())
            val materialRepository = MaterialRepository(
                materialDao = db.materialDao(),
                docDao = db.materialDocDao(),
                imgDao = db.materialImgDao(),
                linkDao = db.materialLinkDao()
            )

            @Suppress("UNCHECKED_CAST")
            return BibliotecaViewModel(
                cursoRepository,
                semanaRepository,
                materialRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}