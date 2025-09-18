package com.example.agendaencarta2004v3

import android.app.Application
import com.example.agendaencarta2004v3.actividades.reminder.NotifUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Crear el canal de notificaciones una sola vez
        NotifUtils.ensureChannel(this)
    }
}