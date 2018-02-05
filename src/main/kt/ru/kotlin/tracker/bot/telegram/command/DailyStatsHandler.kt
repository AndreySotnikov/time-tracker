package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.Constants
import ru.kotlin.tracker.bot.api.DatabaseService
import ru.kotlin.tracker.bot.services.WorktimeCalculationService
import ru.kotlin.tracker.common.AppContext
import java.time.LocalDateTime

class DailyStatsHandler(dbService: DatabaseService) : AbstractCommandHandler(dbService) {

    private val calculationService = AppContext.instance.get(WorktimeCalculationService::class)

    override fun test(text: String): Boolean {
        return text.startsWith("Дневная статистика")
    }

    override fun handle(message: Message): SendMessage {
        val stats = dbService.dailyStats(message.from.id, LocalDateTime.now(Constants.ZONE_ID))
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chatId!!)
        sendMessage.replyMarkup = getKeyboard()
        sendMessage.setParseMode("Markdown")
        sendMessage.text = if (stats == null) "Статистики за текущий день нет" else calculationService.dailyStatsToString(stats)
        return sendMessage
    }

}