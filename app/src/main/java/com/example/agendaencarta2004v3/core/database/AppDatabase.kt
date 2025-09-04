package com.example.agendaencarta2004v3.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.agendaencarta2004v3.actividades.dao.ActividadDao
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import com.example.agendaencarta2004v3.agenda.dao.EventoDao
import com.example.agendaencarta2004v3.agenda.entity.EventoEntity

import com.example.agendaencarta2004v3.biblioteca.dao.CursoDao
import com.example.agendaencarta2004v3.biblioteca.dao.MaterialDao
import com.example.agendaencarta2004v3.biblioteca.dao.SemanaDao
import com.example.agendaencarta2004v3.biblioteca.entity.CursoEntity
import com.example.agendaencarta2004v3.biblioteca.entity.MaterialEntity
import com.example.agendaencarta2004v3.biblioteca.entity.SemanaEntity


@Database(
    entities = [
        EventoEntity::class,
        CursoEntity::class,
        SemanaEntity::class,
        MaterialEntity::class,
        ActividadEntity::class
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventoDao(): EventoDao
    abstract fun cursoDao(): CursoDao
    abstract fun semanaDao(): SemanaDao
    abstract fun materialDao(): MaterialDao
    abstract fun actividadDao(): ActividadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_agenda.db"
                )
                    .fallbackToDestructiveMigration() // 🔹 Para evitar crash en cambios de version
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}