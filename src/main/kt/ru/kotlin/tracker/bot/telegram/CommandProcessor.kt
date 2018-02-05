package ru.kotlin.tracker.bot.telegram

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.bot.api.DatabaseService
import ru.kotlin.tracker.bot.telegram.command.*

class CommandProcessor(dbService: DatabaseService) {

    private val processors = listOf(
            StartHandler(dbService),
            ArriveHandler(dbService),
            StartBreakHandler(dbService),
            EndBreakHandler(dbService),
            GoneHandler(dbService),
            DailyStatsHandler(dbService),
            WeeklyStatsHandler(dbService),
            GoneHandler(dbService),
            DefaultHandler(dbService)
    )


    fun process(message: Message): SendMessage {
        val text = message.text
        val commandHandler = processors.find { it.test(text) }
        return commandHandler?.handle(message) ?: SendMessage()
    }
}