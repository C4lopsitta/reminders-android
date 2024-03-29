package cc.atomtech.todo

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "reminders")
data class Reminder(
    //main data
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "body") var body: String?,
    @ColumnInfo(name = "isCompleted") var isCompleted: Boolean,
    @ColumnInfo(name = "title") var title: String?, /*db v2 added*/
    @ColumnInfo(name = "getNotification") var getNotification: Boolean, /*db v3 added*/
    @ColumnInfo(name = "notificationTimestamp") var notificationTimestamp: Int?, /*db v4 added */
    @ColumnInfo(name = "creationTimestamp") var creationTimestamp: Long?
)

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders")
    suspend fun getReminders() : List<Reminder>

    @Query("SELECT * FROM reminders WHERE isCompleted = 1")
    suspend fun getCompletedReminders() : List<Reminder>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0")
    suspend fun getUncompletedReminders() : List<Reminder>

    @Query("SELECT * FROM reminders WHERE getNotification = 1")
    suspend fun getRemindersWithNotification() : List<Reminder>

    @Query("SELECT * FROM reminders WHERE getNotification = 0")
    suspend fun getRemindersWithoutNotification() : List<Reminder>

    @Query("SELECT * FROM reminders WHERE notificationTimestamp > :time")
    suspend fun getRemindersByNotification(time: Int) : List<Reminder>

    @Query("UPDATE reminders SET isCompleted = :isCompleted WHERE id = :id")
    fun updateIsCompleted(isCompleted: Boolean, id: Long)

    @Query("UPDATE reminders SET getNotification = :getNotification WHERE id = :id")
    fun updateGetNotification(getNotification: Boolean, id: Long)
    @Query("UPDATE reminders SET body = :body, title = :title WHERE id = :id")
    fun updateBodyAndTitle(title: String?, body: String?, id: Long)

    @Query("DELETE FROM reminders WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM reminders")
    fun deleteAll()

    @Update
    fun update(reminder: Reminder)

    @Insert
    fun addReminder(reminder: Reminder): Long

    @Delete
    fun deleteReminder(reminder: Reminder)
}

val migrationV1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE reminders ADD COLUMN title TEXT")
        database.execSQL("UPDATE reminders SET title = \"REMINDER TITLE\"")
    }
}
val migrationV2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE reminders ADD COLUMN getNotification INTEGER NOT NULL default 0")
    }
}
val migrationV1_3 = object : Migration(1, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE reminders ADD COLUMN title TEXT")
        database.execSQL("ALTER TABLE reminders ADD COLUMN getNotification INTEGER NOT NULL default 0")
        database.execSQL("UPDATE reminders SET title = \"Tap to view reminder\"")
    }
}
val migrationV3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE reminders ADD COLUMN notificationTimestamp INTEGER");
    }
}
val migrationV4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE reminders ADD COLUMN creationTimestamp INTEGER");
    }
}

@Database(entities = [Reminder::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao() : ReminderDao
}

lateinit var db: RoomDatabase;
lateinit var reminderDao: ReminderDao;
