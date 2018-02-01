package ru.kotlin.tracker.bot.telegram.handlers

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramWebhookBot
import ru.kotlin.tracker.Constants
import ru.kotlin.tracker.bot.api.DatabaseService
import ru.kotlin.tracker.bot.telegram.CommandProcessor

class TimeManagementWebhookBot(service: DatabaseService) : TelegramWebhookBot() {

    private val logger = LoggerFactory.getLogger(TimeManagementWebhookBot::class.java)
    private val commandProcessor = CommandProcessor(service)

    override fun onWebhookUpdateReceived(update: Update?): BotApiMethod<*> {
        try {
            logger.debug("Received message {}", update)
            if (update != null && update.hasMessage()) {
                return commandProcessor.process(update.message)
            }
        } catch (e: Exception) {
            logger.error("Error while receiving message {}", update, e)
        }
        return SendMessage().setReplyToMessageId(update?.updateId)
    }

    override fun getBotPath(): String = Constants.BOT_NAME

    override fun getBotUsername() = Constants.BOT_NAME

    override fun getBotToken() = Constants.BOT_TOKEN

}
