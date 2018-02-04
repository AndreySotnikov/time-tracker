package ru.kotlin.tracker.bot

import org.mongodb.morphia.converters.SimpleValueConverter
import org.mongodb.morphia.converters.TypeConverter
import org.mongodb.morphia.mapping.MappedField
import ru.kotlin.tracker.Constants
import java.time.LocalDateTime
import java.util.*


class LocalDateTimeConverter : TypeConverter(LocalDateTime::class.java), SimpleValueConverter {

    override fun decode(targetClass: Class<*>, fromDBObject: Any?, optionalExtraInfo: MappedField): Any? {
        if (fromDBObject == null) {
            return null
        }

        if (fromDBObject is Date) {
            return fromDBObject.toInstant().atZone(Constants.ZONE_ID).toLocalDateTime()
        }

        if (fromDBObject is LocalDateTime) {
            return fromDBObject
        }

        // TODO: decode other types

        throw IllegalArgumentException(String.format("Cannot decode object of class: %s", fromDBObject.javaClass.name))
    }

    override fun encode(value: Any?, optionalExtraInfo: MappedField?): Any? {
        if (value == null) {
            return null
        }

        if (value is Date) {
            return value
        }

        if (value is LocalDateTime) {
            val zoned = value.atZone(Constants.ZONE_ID)
            return Date.from(zoned.toInstant())
        }

        throw IllegalArgumentException(String.format("Cannot encode object of class: %s", value.javaClass.name))
    }
}