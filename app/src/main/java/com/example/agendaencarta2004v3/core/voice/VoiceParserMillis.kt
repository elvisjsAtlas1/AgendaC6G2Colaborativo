package com.example.agendaencarta2004v3.core.voice

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.Normalizer
import java.time.*
import java.util.Locale
import java.time.LocalDate
import java.time.ZoneId
data class ParsedVoiceResultMillis(
    val descripcion: String,
    val cursoId: Int?,
    val fechaEntregaMillis: Long?,
    val avisoMinAntes: Int?
)

object VoiceParserMillis {

    private val meses = mapOf(
        "enero" to 1, "febrero" to 2, "marzo" to 3, "abril" to 4, "mayo" to 5, "junio" to 6,
        "julio" to 7, "agosto" to 8, "septiembre" to 9, "setiembre" to 9, "octubre" to 10,
        "noviembre" to 11, "diciembre" to 12
    )
    private val diasSemana = listOf("lunes","martes","miercoles","jueves","viernes","sabado","domingo")

    // Fechas
    private val reFechaCompacta = Regex("""\b(\d{1,2})[\/\-](\d{1,2})(?:[\/\-](\d{2,4}))?\b""")
    private val reFechaMesTexto = Regex(
        """\b(\d{1,2})\s+de\s+([a-záéíóú]+)(?:\s+de\s+(\d{4}))?\b""",
        RegexOption.IGNORE_CASE
    )

    // Horas
    private val reHora24 = Regex("""\b([01]?\d|2[0-3]):([0-5]\d)\b""")
    private val reHora12 = Regex("""\b(\d{1,2})(?::([0-5]\d))?\s*(a\.?m\.?|p\.?m\.?|am|pm)\b""", RegexOption.IGNORE_CASE)
    // Nota: trabajamos sobre texto normalizado sin tildes (manana/tarde/noche)
    private val reHoraTexto =
        Regex("""\ba\s+las\s+(\d{1,2})(?:\s*y\s*(media|cuarto))?(?:\s+de\s+la\s+(manana|tarde|noche))?\b""")
    private val reHoraMenos =
        Regex("""\ba\s+las\s+(\d{1,2})\s+menos\s+(cuarto|media)\b""")

    // Recordatorio: "avísame/recordatorio/recuerdame ... N (día/s|hora/s|min) antes"
    private val reAviso = Regex(
        """\b(?:avisame|recordatorio|recuerdame)\s+(?:con\s+)?(un|una|\d+)\s*(dias?|horas?|min(?:utos)?)\s+antes\b"""
    )

    private val reCurso = Regex(
        """\b(?:de(l)?\s+curso\s+|de\s+|para\s+|curso\s+)([a-z0-9áéíóúüñ][\wáéíóúüñ\s\.\-]{2,})""",
        RegexOption.IGNORE_CASE
    )

    data class CursoRow(val id: Int, val nombre: String)

    fun parse(
        raw: String,
        cursosBD: List<CursoRow>,
        zona: ZoneId = ZoneId.systemDefault(),
        ahora: LocalDate = LocalDate.now(),
        defaultHour: Int = 23,
        defaultMinute: Int = 59
    ): ParsedVoiceResultMillis {
        val texto = normaliza(raw)
        var desc = texto

        // ---- 1) Curso (texto) ----
        val cursoNameRaw = reCurso.find(texto)?.groupValues?.getOrNull(2)?.trim()?.let {
            it.split(Regex("""\s+(?:para|el|la|los|las|hoy|manana|pasado\s+manana|:)\s+"""))
                .first()
                .trim()
        }
        if (!cursoNameRaw.isNullOrBlank()) desc = desc.replaceFirst(reCurso, "").trim()

        // ---- 2) Recordatorio (aviso) ----
        var avisoMinAntes: Int? = null
        reAviso.find(texto)?.let { m ->
            val cantidadTxt = m.groupValues[1].lowercase(Locale.getDefault())
            val unidad = m.groupValues[2].lowercase(Locale.getDefault())
            val n = when (cantidadTxt) {
                "un", "una" -> 1
                else -> cantidadTxt.toIntOrNull() ?: 0
            }
            avisoMinAntes = when {
                unidad.startsWith("dia") -> n * 24 * 60
                unidad.startsWith("hora") -> n * 60
                unidad.startsWith("min") -> n
                else -> null
            }
            desc = desc.replace(m.value, "").trim()
        }

        // ---- 3) Fecha relativa / día semana / explícita ----
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
        if (fechaEntrega == null) {
            for (nombre in diasSemana) {
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
                    val delta = ((targetIdx - hoyIdx) + 7) % 7   // Int
                    val days = if (delta == 0) 7L else delta.toLong()
                    fechaEntrega = ahora.plusDays(days)
                    desc = desc.replace(nombre, "", ignoreCase = true).trim()
                    break // salimos al primer match
                }
            }
        }
        if (fechaEntrega == null) {
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
        }
        if (fechaEntrega != null && fechaEntrega.isBefore(ahora.minusMonths(2))) {
            fechaEntrega = fechaEntrega.plusYears(1)
        }

        // ---- 4) Hora (si hay; si no, defaults) ----
        var hora: Int? = null
        var minuto: Int? = null

        // 24h HH:mm
        reHora24.find(texto)?.let { m ->
            hora = m.groupValues[1].toInt()
            minuto = m.groupValues[2].toInt()
            desc = desc.replace(m.value, "").trim()
        }

        // 12h con am/pm
        if (hora == null) {
            reHora12.find(texto)?.let { m ->
                var h = m.groupValues[1].toInt()
                val min = m.groupValues[2].takeIf { it.isNotBlank() }?.toInt() ?: 0
                val suf = m.groupValues[3].lowercase(Locale.getDefault())
                if (suf.contains("p")) { if (h in 1..11) h += 12 }
                if (suf.contains("a")) { if (h == 12) h = 0 }
                hora = h; minuto = min
                desc = desc.replace(m.value, "").trim()
            }
        }

        // Texto: "a las 5", "a las cinco y media", "a las 10 de la tarde"
        if (hora == null) {
            reHoraTexto.find(texto)?.let { m ->
                var h = m.groupValues[1].toInt()
                val y = m.groupValues.getOrNull(2)?.lowercase()?.trim() // media|cuarto
                val momento = m.groupValues.getOrNull(3)?.lowercase()?.trim() // manana|tarde|noche
                var min = when (y) {
                    "media" -> 30
                    "cuarto" -> 15
                    else -> 0
                }
                when (momento) {
                    "tarde", "noche" -> if (h in 1..11) h += 12
                }
                hora = h; minuto = min
                desc = desc.replace(m.value, "").trim()
            }
        }

        // Texto: "a las 5 menos cuarto/media"
        if (hora == null) {
            reHoraMenos.find(texto)?.let { m ->
                var h = m.groupValues[1].toInt()
                val menos = m.groupValues[2].lowercase()
                val restar = if (menos == "cuarto") 15 else 30
                h = (h - 1).coerceAtLeast(0)
                hora = h; minuto = 60 - restar
                desc = desc.replace(m.value, "").trim()
            }
        }

        val h = hora ?: defaultHour.coerceIn(0, 23)
        val min = (minuto ?: defaultMinute.coerceIn(0, 59)).coerceIn(0, 59)

        val fechaEntregaMillis = fechaEntrega
            ?.atTime(h, min)
            ?.atZone(zona)
            ?.toInstant()
            ?.toEpochMilli()

        val descripcion = desc.trim().trimStart(':', '-', '.', ' ').ifBlank { "Actividad" }
        val cursoId = matchCursoId(cursoNameRaw, cursosBD)

        return ParsedVoiceResultMillis(
            descripcion = descripcion,
            cursoId = cursoId,
            fechaEntregaMillis = fechaEntregaMillis,
            avisoMinAntes = avisoMinAntes
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