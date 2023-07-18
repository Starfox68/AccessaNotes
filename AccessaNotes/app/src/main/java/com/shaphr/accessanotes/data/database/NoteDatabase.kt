package com.shaphr.accessanotes.data.database

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Note::class], version = 18)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNoteDataAccess(): NoteDataAccess
    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration() // increment version number with losing data. must be removed and replaced with proper migrations
                    .addCallback(CALLBACK)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val tables = arrayOf("Note")

                tables.forEach {
                    db.execSQL("CREATE TRIGGER IF NOT EXISTS LogDeletion$it " +
                            "BEFORE DELETE ON $it FOR EACH ROW BEGIN " +
                            "INSERT INTO DeleteLog (tableName, idPrimary, deletedAt) " +
                            "VALUES ('$it', old.id, strftime('%s','now') || substr(strftime('%f','now'),4)); " +
                            "END;"
                    )
                }

                db.execSQL("CREATE TRIGGER IF NOT EXISTS LogDeletionTagNoteCrossRef " +
                        "BEFORE DELETE ON TagNoteCrossRef FOR EACH ROW BEGIN " +
                        "INSERT INTO DeleteLog (tableName, idPrimary, idSecondary, deletedAt) " +
                        "VALUES ('TagNoteCrossRef', old.tagId, old.noteId, strftime('%s','now') || substr(strftime('%f','now'),4)); " +
                        "END;"
                )
            }
        }
    }
}