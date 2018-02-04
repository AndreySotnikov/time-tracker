package ru.kotlin.tracker.bot

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import org.telegram.telegrambots.api.objects.User
import ru.kotlin.tracker.Constants
import ru.kotlin.tracker.bot.api.DatabaseService
import ru.kotlin.tracker.bot.dto.DailyStatsDto
import ru.kotlin.tracker.bot.model.Break
import ru.kotlin.tracker.bot.model.Workday
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MongoService : DatabaseService {

    private val client: MongoClient
    private val datastore: Datastore

    init {

        val uri = MongoClientURI(Constants.MONGO_URI)
        client = MongoClient(uri)

        val morphia = Morphia()

        morphia.mapPackage("ru.kotlin.tracker.model")

        morphia.mapper.converters.addConverter(LocalDateTimeConverter())

        datastore = morphia.createDatastore(client, "heroku_m2qzqvsd")
        datastore.ensureIndexes()
    }

    override fun registerUser(user: User): Boolean {
        //        MongoCollection<TMUser> users = database.getCollection("users", TMUser.class);
        //        long count = users.count(eq("userId", user.getId()));
        //        if (count == 0) {
        //            TMUser newUser = new TMUser();
        //            newUser.setName(user.getUserName());
        //            newUser.setUserId(user.getId());
        //            users.insertOne(newUser);
        //            return true;
        //        }
        return true
    }

    override fun arrived(userId: Int?, time: LocalDateTime): LocalDateTime {
        val startTime = time.truncatedTo(ChronoUnit.DAYS).withHour(0)
        val endTime = time.plusDays(1).truncatedTo(ChronoUnit.DAYS).withHour(0)

        val query = datastore.createQuery(Workday::class.java)
                .filter("creationDate >=", startTime)
                .filter("creationDate <", endTime)
                .filter("userId", userId)

        val updateOperations = datastore.createUpdateOperations(Workday::class.java)
                .set("userId", userId)
                .setOnInsert("creationDate", LocalDateTime.now(Constants.ZONE_ID))
                .set("arrivalTime", time)

        datastore.update(query, updateOperations, true)
        return time
    }

    override fun leave(userId: Int?, time: LocalDateTime): LocalDateTime {
        val startTime = time.truncatedTo(ChronoUnit.DAYS).withHour(0)
        val endTime = time.plusDays(1).truncatedTo(ChronoUnit.DAYS).withHour(0)

        val query = datastore.createQuery(Workday::class.java)
                .filter("creationDate >=", startTime)
                .filter("creationDate <", endTime)
                .filter("userId", userId)

        val updateOperations = datastore.createUpdateOperations(Workday::class.java)
                .set("userId", userId)
                .setOnInsert("creationDate", LocalDateTime.now(Constants.ZONE_ID))
                .set("leaveTime", time)

        datastore.update(query, updateOperations, true)
        return time
    }

    override fun startBreak(userId: Int?, time: LocalDateTime): LocalDateTime? {
        val startTime = time.truncatedTo(ChronoUnit.DAYS).withHour(0)
        val endTime = time.plusDays(1).truncatedTo(ChronoUnit.DAYS).withHour(0)

        val workdays = datastore.createQuery(Workday::class.java)
                .filter("creationDate >=", startTime)
                .filter("creationDate <", endTime)
                .filter("userId", userId).asList()
        if (!workdays.isEmpty()) {
            val workday = workdays[0]
            val breaks = workday.breaks
            val alreadyStartedBreak = breaks.stream()
                    .filter { (from, to) -> from != null && to == null }
                    .findFirst()
                    .orElse(null)
            if (alreadyStartedBreak == null) {
                val b = Break()
                b.from = time
                breaks.add(b)
            } else {
                return null
            }
            datastore.merge(workday)
        }
        return time
    }

    override fun endBreak(userId: Int?, time: LocalDateTime): LocalDateTime? {
        val startTime = time.truncatedTo(ChronoUnit.DAYS).withHour(0)
        val endTime = time.plusDays(1).truncatedTo(ChronoUnit.DAYS).withHour(0)

        val workdays = datastore.createQuery(Workday::class.java)
                .filter("creationDate >=", startTime)
                .filter("creationDate <", endTime)
                .filter("userId", userId).asList()
        if (!workdays.isEmpty()) {
            val workday = workdays[0]
            val breaks = workday.breaks
            val alreadyStartedBreak = breaks.stream()
                    .filter { (from, to) -> from != null && to == null }
                    .findFirst()
                    .orElse(null)
            if (alreadyStartedBreak != null) {
                alreadyStartedBreak.to = time
                datastore.merge(workday)
            } else {
                return null
            }
        }
        return time
    }

    override fun stats(userId: Int?, time: LocalDateTime): DailyStatsDto? {

        val startTime = time.truncatedTo(ChronoUnit.DAYS).withHour(0)
        val endTime = time.plusDays(1).truncatedTo(ChronoUnit.DAYS).withHour(0)

        val workdays = datastore.createQuery(Workday::class.java)
                .filter("creationDate >=", startTime)
                .filter("creationDate <", endTime)
                .filter("userId", userId).asList()

        if (!workdays.isEmpty()) {
            val (_, _, creationDate, arrivalTime, leaveTime, breaks) = workdays[0]
            val breakDtoList = breaks
                    .map({ (from, to) -> DailyStatsDto.BreakDto(from, to) })
            return DailyStatsDto(creationDate, arrivalTime,
                    leaveTime, breakDtoList)
        }
        return null
    }

}
