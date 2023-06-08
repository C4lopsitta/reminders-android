package cc.atomtech.todo

import androidx.room.*

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "body") var body: String?,
    @ColumnInfo(name = "isCompleted") var isCompleted: Boolean
)

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders")
    suspend fun getReminders() : List<Reminder>

    @Query("SELECT * FROM reminders WHERE isCompleted = true")
    suspend fun getCompletedReminders() : List<Reminder>

    @Query("UPDATE reminders SET isCompleted = :isCompleted WHERE id = :id")
    fun updateIsCompleted(isCompleted: Boolean, id: Long)

    @Query("UPDATE reminders SET body = :body WHERE id = :id")
    fun updateBody(body: String?, id: Long)

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

@Database(entities = [Reminder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao() : ReminderDao
}

lateinit var db: RoomDatabase
lateinit var reminderDao: ReminderDao
