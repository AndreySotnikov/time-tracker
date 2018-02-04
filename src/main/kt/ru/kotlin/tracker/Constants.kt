package ru.kotlin.tracker

import java.lang.System.getProperty
import java.lang.System.getenv
import java.time.ZoneId

class Constants {
    companion object {
        val IS_PROD: Boolean = getenv("BOT_ENV") == "PROD"
        val BOT_NAME = if (IS_PROD) getenv("BOT_NAME") else getProperty("bot.name")
        val BOT_TOKEN = if (IS_PROD) getenv("BOT_TOKEN") else getProperty("bot.token")
        val MONGO_URI = if (IS_PROD) getenv("MONGO_URI") else getProperty("mongo.uri")
        val ZONE_ID = ZoneId.of("Europe/Moscow")
    }
}