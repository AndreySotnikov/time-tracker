package ru.kotlin.tracker.bot.services

import ru.kotlin.tracker.bot.dto.DailyStatsDto
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WorktimeCalculationServiceImpl : WorktimeCalculationService {

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        private val SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM")
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    }

    override fun calculateWorkTime(dailyStats: DailyStatsDto): Duration {
        dailyStats.initDefaults()
        val totalDuration = Duration.between(dailyStats.arrived, dailyStats.gone)
        if (dailyStats.breaks.isNotEmpty()) {
            val breaksDuration = dailyStats.breaks
                    .map { b -> Duration.between(b.start, b.end) }
                    .reduce({ obj, duration -> obj.plus(duration) })
            return totalDuration.minus(breaksDuration)
        }
        return totalDuration
    }


    override fun weeklyStatsToString(weeklyStats: List<DailyStatsDto>): String {
        var totalDuration = Duration.ZERO
        val sb = StringBuilder()
        sb.append('*')
                .append(weeklyStats.first().date.format(SHORT_DATE_FORMATTER))
                .append("-")
                .append(weeklyStats.last().date.format(SHORT_DATE_FORMATTER))
                .append("*\n")
        weeklyStats.forEach({
            val workTime = calculateWorkTime(it)
            totalDuration = totalDuration.plus(workTime)
            val workDurStr = LocalTime.MIDNIGHT.plus(workTime).format(TIME_FORMATTER)
            sb.append('_').append(it.date.format(SHORT_DATE_FORMATTER)).append(" :_ ").append(workDurStr).append("\n")
        })
        sb.append("------------------\n")
                .append("_Total :_ ")
                .append(totalDuration.toMinutes() / 60)
                .append(':')
                .append(totalDuration.toMinutes() % 60)

        return sb.toString()
    }

    override fun dailyStatsToString(dailyStats: DailyStatsDto): String {

        val dateStr = dailyStats.date.format(DATE_FORMATTER)
        val arrivedStr = if (dailyStats.arrived == null) "" else dailyStats.arrived!!.format(TIME_FORMATTER)
        val goneStr = if (dailyStats.gone == null) "" else dailyStats.gone!!.format(TIME_FORMATTER)

        val sb = StringBuilder()

        if (!dailyStats.breaks.isEmpty()) {
            sb.append("_Перерывы:_\n")
            dailyStats.breaks.map { b ->
                val startStr = if (b.start == null) "..." else b.start!!.format(TIME_FORMATTER)
                val endStr = if (b.end == null) "..." else b.end!!.format(TIME_FORMATTER)
                startStr + " - " + endStr + "\n"
            }
                    .forEach({ sb.append(it) })
        }

        val workTime = calculateWorkTime(dailyStats)
        val workDurStr = LocalTime.MIDNIGHT.plus(workTime).format(TIME_FORMATTER)

        return String.format("*%s*\n" +
                "_Пришел:_ %s\n" +
                "_Ушел:_ %s\n" +
                "%s" +
                "_Рабочее время:_ %s", dateStr, arrivedStr, goneStr, sb.toString(), workDurStr)
    }
}