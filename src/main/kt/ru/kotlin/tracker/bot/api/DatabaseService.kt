package ru.kotlin.tracker.bot.api

import org.telegram.telegrambots.api.objects.User
import ru.kotlin.tracker.bot.dto.DailyStatsDto
import java.time.LocalDateTime

interface DatabaseService {
    fun registerUser(user: User): Boolean
    fun arrived(userId: Int?, time: LocalDateTime): LocalDateTime
    fun leave(userId: Int?, time: LocalDateTime): LocalDateTime
    fun startBreak(userId: Int?, time: LocalDateTime): LocalDateTime?
    fun endBreak(userId: Int?, time: LocalDateTime): LocalDateTime?
    fun dailyStats(userId: Int?, time: LocalDateTime): DailyStatsDto?
    fun weeklyStats(userId: Int?, time: LocalDateTime): List<DailyStatsDto>
}