package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.bot.api.DatabaseService

class StartHandler(dbService: DatabaseService) : AbstractCommandHandler(dbService) {
    override fun test(text: String): Boolean {
        return text.startsWith("/start")
    }

    override fun handle(message: Message): SendMessage {
        val registered = dbService.registerUser(message.from)
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chatId!!)
        sendMessage.text = if (registered) "Пользователь зарегистрирован" else "Пользователь уже был зарегистрирован"
        sendMessage.replyMarkup = getKeyboard()
        return sendMessage
    }

}