package cc.atomtech.todo

import android.content.ClipboardManager
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
    @ColumnInfo(name = "getNotification") var getNotification: Boolean //db v3 added
    //TODO: Add date/time col
)

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders")
    suspend fun getReminders() : List<Reminder>

    @Query("SELECT * FROM reminders WHERE isCompleted = true")
    suspend fun getCompletedReminders() : List<Reminder>

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
    fun addReminder(reminder: Reminder)

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
        database.execSQL("UPDATE reminders SET title = \"REMINDER TITLE\"")
    }
}
@Database(entities = [Reminder::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao() : ReminderDao
}



lateinit var db: RoomDatabase
lateinit var reminderDao: ReminderDao
lateinit var clipboard: ClipboardManager


