package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.bot.api.DatabaseService

class DefaultHandler(dbService: DatabaseService) : AbstractCommandHandler(dbService) {
    override fun test(text: String): Boolean {
        return true
    }

    override fun handle(message: Message): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chatId!!)
        sendMessage.replyMarkup = getKeyboard()
        sendMessage.text = "Неверная команда"
        return sendMessage
    }

}