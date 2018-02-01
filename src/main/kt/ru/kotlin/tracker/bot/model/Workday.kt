package ru.kotlin.tracker.bot.model

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import java.time.LocalDateTime

@Entity("workdays")
data class Workday(
        @Id val id: ObjectId? = null,
        val userId: Long? = null,
        var creationDate: LocalDateTime? = null,
        var arrivalTime: LocalDateTime? = null,
        var leaveTime: LocalDateTime? = null,
        @Embedded var breaks: MutableList<Break> = arrayListOf(),
        var trackerTime: Double? = null)