package ru.kotlin.tracker.bot.telegram

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import ru.kotlin.tracker.Constants
import ru.kotlin.tracker.bot.api.DatabaseService
import ru.kotlin.tracker.bot.telegram.handlers.TimeManagementLongPollingBot
import ru.kotlin.tracker.bot.telegram.handlers.TimeManagementWebhookBot
import ru.kotlin.tracker.common.AppContext

class TelegramInitializer {

    private val logger: Logger = LoggerFactory.getLogger(TelegramInitializer::class.java)

    private val dbService = AppContext.instance.get(DatabaseService::class)

    fun start() {
        logger.info("Initializing Telegram API")
        try {
            ApiContextInitializer.init()
            registerBot(createApi())
        } catch (e: Exception) {
            logger.error("Error while initializing Telegram API")
        }
    }

    private fun registerBot(botsApi: TelegramBotsApi) {
        if (Constants.IS_PROD)
            botsApi.registerBot(TimeManagementWebhookBot(dbService))
        botsApi.registerBot(TimeManagementLongPollingBot(dbService))
    }

    private fun createApi(): TelegramBotsApi {
        val port: String = if (System.getenv("PORT") == null) "443" else System.getenv("PORT")
        if (Constants.IS_PROD)
            return TelegramBotsApi("https://shrouded-spire-19744.herokuapp.com:443",
                    "https://0.0.0.0:" + port)
        return TelegramBotsApi()
    }
}