package ru.kotlin.tracker.bot.model

import java.time.LocalDateTime

data class Break(var from: LocalDateTime? = null, var to: LocalDateTime? = null)

