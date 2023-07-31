package com.shaphr.accessanotes.data.database

import android.content.Context
import androidx.room.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class Converters {
    /**
     * This function converts a Long timestamp to a LocalDate.
     *
     * @param value the timestamp to convert.
     * @return the corresponding LocalDate, or null if the timestamp is null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    /**
     * This function converts a LocalDate to a Long timestamp.
     *
     * @param date the date to convert.
     * @return the corresponding timestamp, or null if the date is null.
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
}

/**
 * The NoteDatabase class is a Room database that includes the Note and NoteItem entities.
 * The Converters class is included to convert between timestamps and LocalDate objects.
 */
@Database(entities = [Note::class, NoteItem::class], version = 20)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    /**
     * This abstract function is a reference to the NoteDataAccess object, which can be used to manipulate the Note and NoteItem entities.
     * @return the NoteDataAccess object.
     */
    abstract fun getNoteDataAccess(): NoteDataAccess

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        /**
         * This function returns an instance of NoteDatabase. If it does not exist, it will be created.
         *
         * @param context the application context.
         * @return the NoteDatabase instance.
         */
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration() // increment version number with losing data. Must be removed and replaced with proper migrations.
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
