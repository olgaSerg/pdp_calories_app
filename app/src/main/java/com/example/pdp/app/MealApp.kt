package com.example.pdp.app

import android.app.Application
import com.example.pdp.db.AppDatabase

class MealApp : Application() {

    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getDatabase(this)
    }

    companion object {
        var db: AppDatabase? = null
    }
}
