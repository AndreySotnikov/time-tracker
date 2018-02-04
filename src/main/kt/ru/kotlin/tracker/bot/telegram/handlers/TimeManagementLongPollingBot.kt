package ru.kotlin.tracker.bot.telegram.handlers

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import ru.kotlin.tracker.Constants
import ru.kotlin.tracker.bot.api.DatabaseService
import ru.kotlin.tracker.bot.telegram.CommandProcessor

class TimeManagementLongPollingBot(service: DatabaseService) : TelegramLongPollingBot() {

    private val logger = LoggerFactory.getLogger(TimeManagementLongPollingBot::class.java)
    private val commandProcessor = CommandProcessor(service)

    override fun onUpdateReceived(update: Update?) {
        try {
            logger.debug("Received message {}", update)
            if (update != null && update.hasMessage()) {
                execute(commandProcessor.process(update.message))
            }
        } catch (e: Exception) {
            logger.error("Error while receiving message {}", update, e)
            execute(SendMessage()
                    .setReplyToMessageId(update?.message?.messageId)
                    .setChatId(update?.message?.chatId)
                    .setText("Неверная команда"))
        }
    }

    override fun getBotToken(): String = Constants.BOT_TOKEN

    override fun getBotUsername(): String = Constants.BOT_NAME
}
