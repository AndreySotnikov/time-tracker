package ru.kotlin.tracker.bot

import org.mongodb.morphia.converters.SimpleValueConverter
import org.mongodb.morphia.converters.TypeConverter
import org.mongodb.morphia.mapping.MappedField
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class LocalDateTimeConverter : TypeConverter(LocalDateTime::class.java), SimpleValueConverter {

    override fun decode(targetClass: Class<*>, fromDBObject: Any?, optionalExtraInfo: MappedField): Any? {
        if (fromDBObject == null) {
            return null
        }

        if (fromDBObject is Date) {
            return fromDBObject.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime()
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
            val zoned = value.atZone(ZoneOffset.systemDefault())
            return Date.from(zoned.toInstant())
        }

        // TODO: encode other types

        throw IllegalArgumentException(String.format("Cannot encode object of class: %s", value.javaClass.name))
    }
}// TODO: Add other date/time supported classes here
// Other java.time classes: LocalDate.class, LocalTime.class
// Arrays: LocalDateTime[].class, etc
