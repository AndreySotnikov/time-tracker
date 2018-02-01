package ru.kotlin.tracker.bot.dto

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DailyStatsDto(private var date: LocalDateTime?,
                    private var arrived: LocalDateTime?,
                    private var gone: LocalDateTime?,
                    private var breaks: List<BreakDto>) {


    data class BreakDto(var start: LocalDateTime?,
                        var end: LocalDateTime?)

    fun calculateWorkTime(): Duration {
        initDefaults()
        val totalDuration = Duration.between(arrived, gone)
        if (breaks.isNotEmpty()) {
            val breaksDuration = breaks
                    .map { b -> Duration.between(b.start, b.end) }
                    .reduce({ obj, duration -> obj.plus(duration) })
            return totalDuration.minus(breaksDuration)
        }
        return totalDuration
    }

    private fun initDefaults() {
        val zoneId = ZoneId.of("Europe/Moscow")
        date = if (date == null) LocalDateTime.now(zoneId) else date
        arrived = if (arrived == null) LocalDateTime.now(zoneId) else arrived
        gone = if (gone == null) LocalDateTime.now(zoneId) else gone
        breaks.forEach { b ->
            if (b.start == null) {
                b.start = b.end
            }
            if (b.end == null) {
                b.end = b.start
            }
        }
    }

    override fun toString(): String {

        val dateStr = if (date == null) "" else date!!.format(DATE_FORMATTER)
        val arrivedStr = if (arrived == null) "" else arrived!!.format(TIME_FORMATTER)
        val goneStr = if (gone == null) "" else gone!!.format(TIME_FORMATTER)

        val sb = StringBuilder()

        if (!breaks.isEmpty()) {
            sb.append("_Перерывы:_\n")
            breaks.map { b ->
                val startStr = if (b.start == null) "..." else b.start!!.format(TIME_FORMATTER)
                val endStr = if (b.end == null) "..." else b.end!!.format(TIME_FORMATTER)
                startStr + " - " + endStr + "\n"
            }
                    .forEach({ sb.append(it) })
        }

        val workTime = calculateWorkTime()
        val workDurStr = LocalTime.MIDNIGHT.plus(workTime).format(TIME_FORMATTER)

        return String.format("*%s*\n" +
                "_Пришел:_ %s\n" +
                "_Ушел:_ %s\n" +
                "%s" +
                "_Рабочее время:_ %s", dateStr, arrivedStr, goneStr, sb.toString(), workDurStr)
    }

    companion object {

        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    }
}