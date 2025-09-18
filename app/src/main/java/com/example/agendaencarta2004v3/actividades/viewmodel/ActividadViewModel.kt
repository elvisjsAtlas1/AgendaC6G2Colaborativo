package com.example.agendaencarta2004v3.actividades.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaencarta2004v3.actividades.entity.ActividadEntity
import com.example.agendaencarta2004v3.actividades.reminder.ReminderScheduler
import com.example.agendaencarta2004v3.actividades.repository.ActividadRepository
import com.example.agendaencarta2004v3.biblioteca.repository.CursoRepository
import com.example.agendaencarta2004v3.core.voice.VoiceParserMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActividadViewModel(
    private val actividadRepo: ActividadRepository,
    private val cursoRepository: CursoRepository,
    private val reminderScheduler: ReminderScheduler             // ← NUEVO
) : ViewModel() {

    // --- FEEDBACK VOZ ---
    private val _ultimoTextoReconocido = MutableStateFlow("")
    val ultimoTextoReconocido: StateFlow<String> = _ultimoTextoReconocido

    // --- LISTA PARA UI ---
    val actividades: StateFlow<List<ActividadEntity>> =
        actividadRepo.getAllActividades()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // --- AGREGAR (ahora con avisoMinAntes) ---
    fun addActividad(
        descripcion: String,
        cursoId: Int,
        fechaEntregaMillis: Long,
        avisoMinAntes: Int?
    ) {
        viewModelScope.launch {
            val entidad = ActividadEntity(
                descripcion = descripcion,
                cursoId = cursoId,
                fechaEntrega = fechaEntregaMillis,
                hecho = false,
                avisoMinAntes = avisoMinAntes
            )
            // Inserta y obtén ID autogenerado (ajusta tu DAO para devolver Long si no lo hace)
            val id = actividadRepo.insertActividadReturningId(entidad)  // <-- crea este método (o recupera de otra forma)
            val saved = entidad.copy(id = id.toInt())
            if (saved.avisoMinAntes != null) {
                reminderScheduler.schedule(saved)
            }
        }
    }

    // --- MARCAR HECHO / DESHECHO ---
    fun toggleActividad(actividad: ActividadEntity, hecho: Boolean) {
        viewModelScope.launch {
            val updated = actividad.copy(hecho = hecho)
            actividadRepo.updateActividad(updated)
            if (hecho) reminderScheduler.cancel(updated.id) else {
                if (updated.avisoMinAntes != null) reminderScheduler.schedule(updated)
            }
        }
    }

    fun deleteActividad(actividad: ActividadEntity) {
        viewModelScope.launch {
            reminderScheduler.cancel(actividad.id)
            actividadRepo.deleteActividad(actividad)
        }
    }

    // --- VOZ (opcional: sin recordatorio por defecto) ---
    fun handleVoiceCommand(spokenText: String) {
        viewModelScope.launch {
            _ultimoTextoReconocido.value = "Procesando: $spokenText"

            val cursosBD = cursoRepository.getAllCursosOnce()
                .map { VoiceParserMillis.CursoRow(id = it.id, nombre = it.nombre) }

            val parsed = VoiceParserMillis.parse(
                raw = spokenText,
                cursosBD = cursosBD,
                defaultHour = 23,
                defaultMinute = 59
            )

            val cursoId = parsed.cursoId
            val fechaMillis = parsed.fechaEntregaMillis

            if (cursoId != null && fechaMillis != null) {
                // ✅ usa tu addActividad que ya agenda/cancela recordatorios
                addActividad(
                    descripcion = parsed.descripcion,
                    cursoId = cursoId,
                    fechaEntregaMillis = fechaMillis,
                    avisoMinAntes = parsed.avisoMinAntes    // ← puede ser null
                )

                val fechaTxt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(fechaMillis))
                val cursoNombre = cursosBD.firstOrNull { it.id == cursoId }?.nombre ?: "Curso"
                val avisoTxt = parsed.avisoMinAntes?.let { " | Aviso: ${humanizeMinutes(it)} antes" } ?: ""

                _ultimoTextoReconocido.value =
                    "✅ Guardado\nCurso: $cursoNombre\nFecha/Hora: $fechaTxt$avisoTxt\nDesc: ${parsed.descripcion}"
            } else {
                val faltantes = buildList {
                    if (cursoId == null) add("curso")
                    if (fechaMillis == null) add("fecha/hora")
                }.joinToString(" y ")
                _ultimoTextoReconocido.value =
                    "⚠️ No pude registrar por falta de $faltantes.\nDetectado: ${parsed.descripcion}"
            }
        }
    }

    /** Convierte minutos a texto amigable: 2880 -> "2 días", 120 -> "2 horas", 15 -> "15 min" */
    private fun humanizeMinutes(mins: Int): String = when {
        mins % (24 * 60) == 0 -> {
            val d = mins / (24 * 60); if (d == 1) "1 día" else "$d días"
        }
        mins % 60 == 0 -> {
            val h = mins / 60; if (h == 1) "1 hora" else "$h horas"
        }
        else -> "$mins min"
    }
}

/*
// 🛠️ Código de depuración para ver la lista de actividades y operaciones en la base de datos
val actividades: StateFlow<List<ActividadEntity>> =
    repo.getAllActividades()
        .onEach { lista ->
            Log.d("ActividadVM", "Lista actual de actividades: $lista") // Mostrar lista cada vez que cambie
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

init {
    // 🛠️ Para depurar: recolecta el flujo y loguea cada actualización
    viewModelScope.launch {
        repo.getAllActividades()
            .onEach { Log.d("ActividadVM", "Lista actual: $it") } // Log alternativo de seguimiento
            .collect() // Importante: sin collect no se ejecuta el flujo
    }
}

fun addActividad(desc: String, cursoId: Int, fechaEntrega: Long) {
    viewModelScope.launch {
        val nueva = ActividadEntity(
            descripcion = desc,
            cursoId = cursoId,
            fechaEntrega = fechaEntrega,
            hecho = false
        )
        Log.d("ActividadVM", "➕ Insertando actividad: $nueva") // Log al agregar nueva actividad
        repo.insertActividad(nueva)
    }
}

fun toggleActividad(actividad: ActividadEntity, hecho: Boolean) {
    viewModelScope.launch {
        val actualizada = actividad.copy(hecho = hecho)
        Log.d("ActividadVM", "✅ Cambiando estado: $actualizada") // Log al cambiar estado de actividad
        repo.updateActividad(actualizada)
    }
}
*/