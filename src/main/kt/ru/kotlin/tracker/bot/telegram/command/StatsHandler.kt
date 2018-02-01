package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.bot.api.DatabaseService
import java.time.LocalDateTime
import java.time.ZoneId

class StatsHandler(dbService: DatabaseService) : AbstractCommandHandler(dbService) {
    override fun test(text: String): Boolean {
        return text.startsWith("Статистика")
    }

    override fun handle(message: Message): SendMessage {
        val stats = dbService.stats(message.from.id, LocalDateTime.now(ZoneId.of("Europe/Moscow")))
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chatId!!)
        sendMessage.replyMarkup = getKeyboard()
        sendMessage.setParseMode("Markdown")
        sendMessage.text = stats!!.toString()
        return sendMessage
    }

}