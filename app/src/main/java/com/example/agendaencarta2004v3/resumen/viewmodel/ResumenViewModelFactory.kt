package com.example.agendaencarta2004v3.resumen.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaencarta2004v3.actividades.repository.ActividadRepository
import com.example.agendaencarta2004v3.core.database.AppDatabase

class ResumenViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResumenViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val actividadRepo = ActividadRepository(db.actividadDao())
            return ResumenViewModel(actividadRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}