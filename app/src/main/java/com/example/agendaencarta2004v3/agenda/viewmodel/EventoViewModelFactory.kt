package com.example.agendaencarta2004v3.agenda.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaencarta2004v3.agenda.repository.EventoRepository
import com.example.agendaencarta2004v3.core.database.AppDatabase

class EventoViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
            val dao = AppDatabase.getDatabase(application).eventoDao() // 👈 cambio aquí
            val repository = EventoRepository(dao)
            @Suppress("UNCHECKED_CAST")
            return EventoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
