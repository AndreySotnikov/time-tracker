package ru.kotlin.tracker.bot.services

import ru.kotlin.tracker.bot.dto.DailyStatsDto
import java.time.Duration

interface WorktimeCalculationService {
    fun calculateWorkTime(dailyStats: DailyStatsDto): Duration
    fun weeklyStatsToString(weeklyStats: List<DailyStatsDto>): String
    fun dailyStatsToString(dailyStats: DailyStatsDto): String
}