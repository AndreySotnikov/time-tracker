package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow
import ru.kotlin.tracker.bot.api.DatabaseService

abstract class AbstractCommandHandler(protected val dbService: DatabaseService) {
    abstract fun test(text: String): Boolean
    abstract fun handle(message: Message): SendMessage

    protected fun getKeyboard(): ReplyKeyboard {
        val keyboardMarkup = ReplyKeyboardMarkup()
        keyboardMarkup.selective = true
        val firstRow = KeyboardRow()
        val secondRow = KeyboardRow()
        val thirdRow = KeyboardRow()
        firstRow.add(KeyboardButton("Пришел"))
        firstRow.add(KeyboardButton("Ушел"))
        secondRow.add(KeyboardButton("Отошел"))
        secondRow.add(KeyboardButton("Вернулся"))
        thirdRow.add(KeyboardButton("Статистика"))
        keyboardMarkup.keyboard.add(firstRow)
        keyboardMarkup.keyboard.add(secondRow)
        keyboardMarkup.keyboard.add(thirdRow)
        return keyboardMarkup
    }
}