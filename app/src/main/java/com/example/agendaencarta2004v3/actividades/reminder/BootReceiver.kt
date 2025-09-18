package com.example.agendaencarta2004v3.actividades.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Reagenda todos los recordatorios futuros desde Room
        ReminderRescheduler.enqueue(context)
    }
}