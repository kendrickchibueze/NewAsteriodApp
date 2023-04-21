package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PictureOfDayDao {
    @Query(MyDatabase.GET_PICTURE_OF_DAY)
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(picture: DatabasePictureOfDay)
}

@Dao
interface AsteroidDao {
    @Query(MyDatabase.GET_ALL_NEAR_EARTH_OBJECTS)
    fun getAllAsteroids() : LiveData<List<DatabaseAsteroid>>

    @Query(MyDatabase.GET_WEEKLY_NEAR_EARTH_OBJECTS)
    fun getWeeklyAsteroids(startDate: String, endDate: String) : LiveData<List<DatabaseAsteroid>>

    @Query(MyDatabase.GET_TODAY_OBJECTS)
    fun getTodayAsteroids(today: String) : LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Database(entities = [DatabasePictureOfDay::class, DatabaseAsteroid::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {



    companion object{
        const val DATABASE_NAME = "MyDatabase"
        const val GET_PICTURE_OF_DAY = """
        SELECT * FROM DatabasePictureOfDay 
        ORDER BY date DESC LIMIT 1
    """
        const val GET_ALL_NEAR_EARTH_OBJECTS = """
        SELECT * FROM DatabaseAsteroid 
        ORDER BY closeApproachDate DESC
    """
        const val GET_WEEKLY_NEAR_EARTH_OBJECTS = """
        SELECT * FROM DatabaseAsteroid 
        WHERE closeApproachDate BETWEEN :startDate AND :endDate 
        ORDER BY closeApproachDate DESC
    """
        const val GET_TODAY_OBJECTS = """
        SELECT * FROM DatabaseAsteroid 
        WHERE closeApproachDate = :today
    """
    }

    abstract val pictureOfDayDao: PictureOfDayDao
    abstract val asteroidDao: AsteroidDao
}



object MyDatabaseProvider {
    private var instance: MyDatabase? = null

    fun getDatabase(context: Context): MyDatabase {
        synchronized(this) {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    MyDatabase.DATABASE_NAME
                ).build()
            }
            return instance as MyDatabase
        }
    }
}

