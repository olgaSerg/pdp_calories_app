package com.example.pdp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "meal_table")
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "meal_name") var mealName: String,
    @ColumnInfo(name = "calories") var calories: Int,
    @ColumnInfo(name = "date") var date: LocalDate,
    @ColumnInfo(name = "time") var time: String
)