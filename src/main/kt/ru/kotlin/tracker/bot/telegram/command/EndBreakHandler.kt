package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.bot.api.DatabaseService
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class EndBreakHandler(dbService: DatabaseService) : AbstractCommandHandler(dbService) {

    override fun test(text: String): Boolean {
        return text.startsWith("Вернулся")
    }

    override fun handle(message: Message): SendMessage {
        val messageTime = LocalDateTime
                .ofInstant(Instant.ofEpochSecond(message.date.toLong()),
                        ZoneId.of("Europe/Moscow"))
        val arrived = dbService.endBreak(message.from.id, messageTime)
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chatId!!)
        sendMessage.replyMarkup = getKeyboard()
        if (arrived == null) {
            sendMessage.text = "Нет незавершенных перерывов"
        } else {
            sendMessage.text = "Вернулся в " + arrived
        }
        return sendMessage
    }
}