package com.example.agendaencarta2004v3.actividades.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaencarta2004v3.actividades.repository.ActividadRepository
import com.example.agendaencarta2004v3.core.database.AppDatabase

class ActividadViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getDatabase(application)
        val repo = ActividadRepository(db.actividadDao())
        return ActividadViewModel(repo) as T
    }
}