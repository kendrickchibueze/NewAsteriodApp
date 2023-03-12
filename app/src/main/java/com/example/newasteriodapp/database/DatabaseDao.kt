package com.example.newasteriodapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface PictureOfDayDao {
    @Query("SELECT * FROM DatabasePictureOfDay pod ORDER BY pod.date DESC LIMIT 0,1")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(picture: DatabasePictureOfDay)
}

@Dao
interface AsteroidDao {
    @Query("select * from DatabaseAsteroid order by closeApproachDate desc")
    fun getAllAsteroids() : LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM DatabaseAsteroid obj WHERE obj.closeApproachDate BETWEEN :startDate  AND :endDate  order by closeApproachDate desc")
    fun getWeeklyAsteroids(startDate: String, endDate: String) : LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM DatabaseAsteroid obj WHERE obj.closeApproachDate = :today")
    fun getTodayAsteroids(today: String) : LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}