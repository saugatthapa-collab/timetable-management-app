package com.example.timetable.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimetableEntry::class], version = 1, exportSchema = false)
abstract class TimetableDatabase : RoomDatabase() {
    abstract fun timetableDao(): TimetableDao

    companion object {
        @Volatile
        private var INSTANCE: TimetableDatabase? = null

        fun getDatabase(context: Context): TimetableDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimetableDatabase::class.java,
                    "timetable_database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // sample data is added through the repository or app logic instead of here
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
