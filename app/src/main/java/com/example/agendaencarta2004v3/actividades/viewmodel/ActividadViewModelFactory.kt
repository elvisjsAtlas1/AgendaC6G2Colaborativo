package com.example.agendaencarta2004v3.actividades.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendaencarta2004v3.actividades.reminder.ReminderScheduler
import com.example.agendaencarta2004v3.actividades.repository.ActividadRepository
import com.example.agendaencarta2004v3.biblioteca.repository.CursoRepository
import com.example.agendaencarta2004v3.core.database.AppDatabase


class ActividadViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActividadViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val actividadRepo = ActividadRepository(db.actividadDao())
            val cursoRepo = CursoRepository(db.cursoDao())
            val scheduler = ReminderScheduler(application) // ⬅️ AÑADIDO

            return ActividadViewModel(
                actividadRepo = actividadRepo,
                cursoRepository = cursoRepo,
                reminderScheduler = scheduler          // ⬅️ pásalo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
