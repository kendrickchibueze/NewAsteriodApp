package com.example.newasteriodapp.database

import android.content.Context
import androidx.room.*

@Database(entities = [DatabasePictureOfDay::class, DatabaseAsteroid::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract val pictureOfDayDao: PictureOfDayDao
    abstract val asteroidDao: AsteroidDao

    companion object {
        const val DATABASE_NAME = "MyDatabase"

        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


