package com.example.pdp.app

import android.app.Application
import androidx.room.Room
import com.example.pdp.db.AppDatabase

class MealApp : Application() {

    companion object {
        var db: AppDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "meal_database"
        ).build()
    }
}
