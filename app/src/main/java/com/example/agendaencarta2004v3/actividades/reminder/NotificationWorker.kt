package com.example.agendaencarta2004v3.actividades.reminder

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.agendaencarta2004v3.MainActivity

class NotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        NotifUtils.ensureChannel(applicationContext)
        if (!NotifUtils.notificationsEnabled(applicationContext)) return Result.success()

        val title = inputData.getString("title") ?: "Actividad próxima"
        val body  = inputData.getString("body")  ?: "Tienes una actividad por realizar"
        val actId = inputData.getInt("actividad_id", 0)

        val intent = Intent(applicationContext, MainActivity::class.java)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }

        val pi = PendingIntent.getActivity(
            applicationContext, actId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(applicationContext, NotifUtils.CHANNEL_ID)
            .setSmallIcon(applicationContext.applicationInfo.icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(actId, notif)
        return Result.success()
    }
}
