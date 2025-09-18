package com.example.agendaencarta2004v3.actividades.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import java.util.concurrent.TimeUnit
import kotlin.jvm.java
import android.os.Build


class ReminderScheduler(private val context: Context) {

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(actividad: ActividadEntity) {
        val avisoMin = actividad.avisoMinAntes ?: return
        val triggerAt = actividad.fechaEntrega - avisoMin * 60_000L
        if (triggerAt <= System.currentTimeMillis()) return

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val i = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", actividad.id)
            putExtra("title", "Entrega de ${actividad.descripcion}")
            putExtra("text", "Revisa el curso antes de la hora límite")
        }

        val pi = PendingIntent.getBroadcast(
            context, actividad.id, i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun cancel(actividadId: Int) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", actividadId)
        }
        val pi = PendingIntent.getBroadcast(
            context, actividadId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
    }
}