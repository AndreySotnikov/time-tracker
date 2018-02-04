package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.Constants
import ru.kotlin.tracker.bot.api.DatabaseService
import java.time.Instant
import java.time.LocalDateTime

class GoneHandler(dbService: DatabaseService) : AbstractCommandHandler(dbService) {

    override fun test(text: String): Boolean {
        return text.startsWith("Ушел")
    }

    override fun handle(message: Message): SendMessage {
        val messageTime = LocalDateTime
                .ofInstant(Instant.ofEpochSecond(message.date.toLong()), Constants.ZONE_ID)
        val left = dbService.leave(message.from.id, messageTime)
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chatId!!)
        sendMessage.replyMarkup = getKeyboard()
        sendMessage.text = "Ушел в " + left
        return sendMessage
    }
}