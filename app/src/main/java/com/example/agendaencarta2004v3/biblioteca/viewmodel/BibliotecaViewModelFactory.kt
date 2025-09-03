package com.example.agendaencarta2004v3.biblioteca.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaencarta2004v3.biblioteca.repository.CursoRepository
import com.example.agendaencarta2004v3.biblioteca.repository.MaterialRepository
import com.example.agendaencarta2004v3.biblioteca.repository.SemanaRepository
import com.example.agendaencarta2004v3.core.database.AppDatabase


class BibliotecaViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application)

        val cursoRepository = CursoRepository(database.cursoDao())
        val semanaRepository = SemanaRepository(database.semanaDao())
        val materialRepository = MaterialRepository(database.materialDao())

        return BibliotecaViewModel(cursoRepository, semanaRepository, materialRepository) as T
    }
}