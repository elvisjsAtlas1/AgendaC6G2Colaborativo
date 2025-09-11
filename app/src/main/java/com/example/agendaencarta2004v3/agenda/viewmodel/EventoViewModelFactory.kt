package com.example.agendaencarta2004v3.agenda.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaencarta2004v3.agenda.repository.EventoRepository
import com.example.agendaencarta2004v3.core.database.AppDatabase

class EventoViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application) // usa Application context
            val dao = db.eventoDao()
            val repository = EventoRepository(dao)
            return EventoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
