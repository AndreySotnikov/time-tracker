package ru.kotlin.tracker

import ru.kotlin.tracker.bot.MongoService
import ru.kotlin.tracker.bot.api.DatabaseService
import ru.kotlin.tracker.bot.telegram.TelegramInitializer
import ru.kotlin.tracker.common.AppContext

fun main(args: Array<String>) {
    initContext()
    val telegramApi = TelegramInitializer()
    telegramApi.start()
}

fun initContext() {
    AppContext.instance.register(DatabaseService::class, MongoService())
}