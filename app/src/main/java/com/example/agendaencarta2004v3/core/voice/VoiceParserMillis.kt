package com.example.agendaencarta2004v3.core.voice

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.Normalizer
import java.time.*
import java.util.Locale
import kotlin.math.min

data class ParsedVoiceResultMillis(
    val descripcion: String,
    val cursoId: Int?,            // desde tu BD
    val fechaEntregaMillis: Long? // solo fecha (con hora por defecto aplicada)
)

object VoiceParserMillis {

    private val meses = mapOf(
        "enero" to 1, "febrero" to 2, "marzo" to 3, "abril" to 4, "mayo" to 5, "junio" to 6,
        "julio" to 7, "agosto" to 8, "septiembre" to 9, "setiembre" to 9, "octubre" to 10,
        "noviembre" to 11, "diciembre" to 12
    )
    private val diasSemana = listOf(
        "lunes","martes","miercoles","jueves","viernes","sabado","domingo"
    )

    private val reFechaCompacta = Regex("""\b(\d{1,2})[\/\-](\d{1,2})(?:[\/\-](\d{2,4}))?\b""")
    private val reFechaMesTexto = Regex("""\b(\d{1,2})\s+de\s+([a-záéíóú]+)(?:\s+de\s+(\d{4}))?\b""", RegexOption.IGNORE_CASE)
    // NO usamos la hora: si aparece, la ignoramos deliberadamente

    private val reCurso = Regex("""\b(?:de(l)?\s+curso\s+|de\s+|para\s+|curso\s+)([a-z0-9áéíóúüñ][\wáéíóúüñ\s\.\-]{2,})""", RegexOption.IGNORE_CASE)

    data class CursoRow(val id: Int, val nombre: String)


    fun parse(
        raw: String,
        cursosBD: List<CursoRow>,
        zona: ZoneId = ZoneId.systemDefault(),
        ahora: LocalDate = LocalDate.now(),
        // si quieres que el “fin de día” sea otro, cámbialo aquí:
        defaultHour: Int = 23,
        defaultMinute: Int = 59
    ): ParsedVoiceResultMillis {
        val texto = normaliza(raw)
        var desc = texto

        // 1) Curso (texto)
        val cursoNameRaw = reCurso.find(texto)?.groupValues?.getOrNull(2)?.trim()?.let {
            // texto ya está normalizado, por eso usamos "manana" y "pasado manana"
            it.split(Regex("""\s+(?:para|el|la|los|las|hoy|manana|pasado\s+manana|:)\s+"""))
                .first()
                .trim()
        }
        if (!cursoNameRaw.isNullOrBlank()) desc = desc.replaceFirst(reCurso, "").trim()

        // 2) Fecha relativa (usar claves SIN tilde porque "normaliza" las quita)
        var fechaEntrega: LocalDate? = null
        when {
            texto.contains("pasado manana") || texto.contains("pasadomanana") -> {
                fechaEntrega = ahora.plusDays(2)
                desc = desc.replace("pasado manana", "", ignoreCase = true)
                    .replace("pasadomanana", "", ignoreCase = true)
                    .trim()
            }
            texto.contains("manana") -> {
                fechaEntrega = ahora.plusDays(1)
                desc = desc.replace("manana", "", ignoreCase = true).trim()
            }
            texto.contains("hoy") -> {
                fechaEntrega = ahora
                desc = desc.replace("hoy", "", ignoreCase = true).trim()
            }
        }
        // Día de semana

        if (fechaEntrega == null) {
            diasSemana.forEach { nombre ->
                if (texto.contains(nombre)) {
                    val targetIdx = when (nombre.take(3)) {
                        "lun" -> 1
                        "mar" -> 2
                        "mie" -> 3
                        "jue" -> 4
                        "vie" -> 5
                        "sab" -> 6
                        else  -> 7 // dom
                    }
                    val hoyIdx = ahora.dayOfWeek.value // 1..7 (Lunes=1)
                    val delta = ((targetIdx - hoyIdx) + 7) % 7
                    fechaEntrega = ahora.plusDays(if (delta == 0) 7 else delta.toLong())
                    desc = desc.replace(nombre, "", ignoreCase = true).trim()
                }
            }
        }

        // 3) Fecha explícita (dd/MM[/yyyy] o “21 de setiembre [de 2025]”)
        val mCompacta = reFechaCompacta.find(texto)
        if (mCompacta != null) {
            val d = mCompacta.groupValues[1].toInt()
            val m = mCompacta.groupValues[2].toInt()
            val y = mCompacta.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() }?.toInt() ?: ahora.year
            fechaEntrega = safeDate(y, m, d)
            desc = desc.replace(mCompacta.value, "").trim()
        } else {
            val mText = reFechaMesTexto.find(texto)
            if (mText != null) {
                val d = mText.groupValues[1].toInt()
                val mesTxt = normaliza(mText.groupValues[2])
                val y = mText.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() }?.toInt() ?: ahora.year
                val mesNum = meses[mesTxt]
                if (mesNum != null) {
                    fechaEntrega = safeDate(y, mesNum, d)
                    desc = desc.replace(mText.value, "").trim()
                }
            }
        }

        // Si dd/MM sin año quedó muy en el pasado, asume año siguiente
        if (fechaEntrega != null && fechaEntrega.isBefore(ahora.minusMonths(2))) {
            fechaEntrega = fechaEntrega.plusYears(1)
        }

        // 4) Aplicar hora por defecto (ignorando cualquier hora del texto)
        val fechaEntregaMillis = fechaEntrega
            ?.atTime(defaultHour.coerceIn(0,23), defaultMinute.coerceIn(0,59))
            ?.atZone(zona)
            ?.toInstant()
            ?.toEpochMilli()

        val descripcion = desc.trim().trimStart(':', '-', '.', ' ').ifBlank { "Actividad" }
        val cursoId = matchCursoId(cursoNameRaw, cursosBD)

        return ParsedVoiceResultMillis(
            descripcion = descripcion,
            cursoId = cursoId,
            fechaEntregaMillis = fechaEntregaMillis
        )
    }

    // ---- helpers ----
    private fun normaliza(s: String): String {
        val base = Normalizer.normalize(s.lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        return base.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").replace(Regex("\\s+"), " ").trim()
    }
    private fun safeDate(y: Int, m: Int, d: Int): LocalDate? = try { LocalDate.of(y, m, d) } catch (_: Exception) { null }

    private fun matchCursoId(nombreDetectado: String?, cursosBD: List<CursoRow>): Int? {
        if (nombreDetectado.isNullOrBlank()) return null
        val target = normaliza(nombreDetectado)
        var bestId: Int? = null
        var bestScore = Int.MAX_VALUE
        for (c in cursosBD) {
            val cand = normaliza(c.nombre)
            val sc = levenshtein(target, cand)
            if (sc < bestScore || (sc == bestScore && cand.startsWith(target))) {
                bestScore = sc
                bestId = c.id
            }
        }
        return if (bestScore <= 4) bestId else null
    }
    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        val dp = IntArray(b.length + 1) { it }
        for (i in 1..a.length) {
            var prev = dp[0]
            dp[0] = i
            for (j in 1..b.length) {
                val temp = dp[j]
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[j] = minOf(dp[j] + 1, dp[j - 1] + 1, prev + cost)
                prev = temp
            }
        }
        return dp[b.length]
    }
}
