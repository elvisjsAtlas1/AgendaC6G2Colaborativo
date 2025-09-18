package com.example.agendaencarta2004v3.actividades.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.agendaencarta2004v3.core.database.AppDatabase


object ReminderRescheduler {
    fun enqueue(context: Context) {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<RescheduleWork>().build())
    }
}

class RescheduleWork(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.actividadDao()

        // crea en tu DAO un método "getAllOnce()" o similar para listar una vez
        val actividades = dao.getAllOnce()  // actividades con avisoMinAntes != null
        val scheduler = ReminderScheduler(applicationContext)
        val now = System.currentTimeMillis()

        actividades.forEach { act ->
            val aviso = act.avisoMinAntes ?: return@forEach
            if (act.hecho) return@forEach
            if (act.fechaEntrega <= now) return@forEach
            val trigger = act.fechaEntrega - aviso * 60_000L
            scheduler.schedule(act.copy()) // schedule(...) ya calcula y valida trigger
        }
        return Result.success()
    }
}