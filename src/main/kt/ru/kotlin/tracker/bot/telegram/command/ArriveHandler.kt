package ru.kotlin.tracker.bot.telegram.command

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import ru.kotlin.tracker.Constants
import ru.kotlin.tracker.bot.api.DatabaseService
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.regex.Pattern

class ArriveHandler(dbService: DatabaseService) : AbstractCommandHandler(dbService) {

    private val pattern = Pattern.compile("^[П|п]риш[е|ё]л\\s+в\\s+((\\d+)[:|\\-|\\s|_](\\d+))")

    override fun test(text: String): Boolean {
        return text.toLowerCase().startsWith("пришел") || text.toLowerCase().startsWith("пришёл")
    }

    override fun handle(message: Message): SendMessage {
        val text = message.text
        val messageTime: LocalDateTime
        messageTime = if (text.toLowerCase() == "пришел") {
            LocalDateTime.ofInstant(Instant.ofEpochSecond(message.date.toLong()), Constants.ZONE_ID)
        } else {
            processArrivedAt(text)
        }
        val arrived = dbService.arrived(message.from.id, messageTime)
        val sendMessage = SendMessage()
        sendMessage.setChatId(message.chatId!!)
        sendMessage.replyMarkup = getKeyboard()
        sendMessage.text = "Пришел в " + arrived
        return sendMessage
    }

    private fun processArrivedAt(text: String): LocalDateTime {
        val matcher = pattern.matcher(text)
        if (matcher.matches()) {
            val date = LocalDate.now(Constants.ZONE_ID)
            val hours = matcher.group(2).toInt()
            val minutes = matcher.group(3).toInt()
            val time = LocalTime.of(hours, minutes)
            return LocalDateTime.of(date, time)
        }
        throw RuntimeException("Неверный формат")
    }
}