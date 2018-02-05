package ru.kotlin.tracker.bot.services

import ru.kotlin.tracker.bot.dto.DailyStatsDto
import java.time.Duration
import java.time.LocalDateTime
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
        val dateRangeStr = formatDateRange(weeklyStats.first().date, weeklyStats.last().date)
        val sb = StringBuilder()
        weeklyStats.forEach({
            val workTime = calculateWorkTime(it)
            totalDuration = totalDuration.plus(workTime)
            sb.append(formatTodayWorktime(it.date, workTime))
        })
        val totalDurationStr = formatTotalDuration(totalDuration)
        return String.format("*Период: %s*\n" +
                "%s" +
                "------------------\n" +
                "_Всего :_ %s", dateRangeStr, sb.toString(), totalDurationStr)
    }

    override fun dailyStatsToString(dailyStats: DailyStatsDto): String {

        val dateStr = dailyStats.date.format(DATE_FORMATTER)
        val arrivedStr = if (dailyStats.arrived == null) "" else dailyStats.arrived!!.format(TIME_FORMATTER)
        val goneStr = if (dailyStats.gone == null) "" else dailyStats.gone!!.format(TIME_FORMATTER)

        val sb = StringBuilder()

        if (!dailyStats.breaks.isEmpty()) {
            sb.append("_Перерывы:_\n")
            dailyStats.breaks
                    .map { formatBreak(it)}
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

    private fun formatBreak(breakData: DailyStatsDto.BreakDto): String {
        val startStr = if (breakData.start == null) "..." else breakData.start!!.format(TIME_FORMATTER)
        val endStr = if (breakData.end == null) "..." else breakData.end!!.format(TIME_FORMATTER)
        return String.format("%s - %s\n", startStr, endStr)
    }

    private fun formatDateRange(from: LocalDateTime, to: LocalDateTime): String {
        return String.format("%s -- %s", from.format(SHORT_DATE_FORMATTER), to.format(SHORT_DATE_FORMATTER))
    }

    private fun formatTodayWorktime(date: LocalDateTime, duration: Duration): String {
        val workDurStr = LocalTime.MIDNIGHT.plus(duration).format(TIME_FORMATTER)
        val dateStr = date.format(SHORT_DATE_FORMATTER)
        return String.format("_%s :_ %s\n", dateStr, workDurStr)
    }

    private fun formatTotalDuration(totalDuration: Duration): String {
        return String.format("%d:%d", totalDuration.toMinutes() / 60, totalDuration.toMinutes() % 60)
    }

}