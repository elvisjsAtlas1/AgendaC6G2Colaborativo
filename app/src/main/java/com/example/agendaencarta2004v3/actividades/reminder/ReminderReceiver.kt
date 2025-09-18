package com.example.agendaencarta2004v3.actividades.reminder

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.agendaencarta2004v3.MainActivity
import com.example.agendaencarta2004v3.R

class ReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val id    = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: "Actividad"
        val text  = intent.getStringExtra("text") ?: "Tienes una entrega próxima"

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val tapPI = PendingIntent.getActivity(
            context, id, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val n = NotificationCompat.Builder(context, NotifUtils.CHANNEL_ID)
            // Usa un ícono existente en tu proyecto:
            .setSmallIcon(R.mipmap.ic_launcher) // o R.drawable.ic_launcher_foreground
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(tapPI)
            .build()

        NotificationManagerCompat.from(context).notify(id, n)
    }
}