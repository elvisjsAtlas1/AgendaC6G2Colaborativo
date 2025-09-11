package com.example.agendaencarta2004v3.resumen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendaencarta2004v3.actividades.repository.ActividadRepository
import kotlinx.coroutines.flow.*
import java.time.*
import java.time.temporal.WeekFields
import java.util.Locale

data class WeekStat(
    val start: LocalDate,  // lunes de esa semana
    val end: LocalDate,    // domingo
    val label: String,     // ej. "22-28 Sep"
    val count: Int
)

class ResumenViewModel(
    private val actividadRepo: ActividadRepository
) : ViewModel() {

    private val zone = ZoneId.systemDefault()
    private val weekFields = WeekFields.of(Locale.getDefault())

    private val semanas = 8

    val stats: StateFlow<List<WeekStat>> =
        actividadRepo.getAllActividades()
            .map { actividades ->
                val hoy = LocalDate.now(zone)
                val inicioVentana = hoy.minusWeeks((semanas - 1).toLong())
                    .with(weekFields.dayOfWeek(), 1) // lunes

                val hechasEnVentana = actividades
                    .asSequence()
                    .filter { it.hecho }
                    .filter { a ->
                        val fecha = Instant.ofEpochMilli(a.fechaEntrega).atZone(zone).toLocalDate()
                        !fecha.isBefore(inicioVentana)
                    }

                val agrupadas = hechasEnVentana.groupBy { a ->
                    val fecha = Instant.ofEpochMilli(a.fechaEntrega).atZone(zone).toLocalDate()
                    // Normaliza al lunes de esa semana
                    fecha.with(weekFields.dayOfWeek(), 1)
                }

                // Construye todas las semanas (aunque cuenten 0)
                (0 until semanas).map { i ->
                    val start = inicioVentana.plusWeeks(i.toLong())
                    val end = start.plusDays(6)
                    val count = agrupadas[start]?.size ?: 0
                    WeekStat(
                        start = start,
                        end = end,
                        label = "${start.dayOfMonth}-${end.dayOfMonth} " +
                                end.month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                        count = count
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}