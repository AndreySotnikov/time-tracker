package ru.kotlin.tracker.bot.dto

import ru.kotlin.tracker.Constants
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DailyStatsDto(var date: LocalDateTime,
                    var arrived: LocalDateTime?,
                    var gone: LocalDateTime?,
                    var breaks: List<BreakDto>) {


    data class BreakDto(var start: LocalDateTime?,
                        var end: LocalDateTime?)

    fun initDefaults() {
        arrived = if (arrived == null) LocalDateTime.now(Constants.ZONE_ID) else arrived
        gone = if (gone == null) LocalDateTime.now(Constants.ZONE_ID) else gone
        breaks.forEach { b ->
            if (b.start == null) {
                b.start = b.end
            }
            if (b.end == null) {
                b.end = gone
            }
        }
    }
}