package com.example.pdp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "meal_table")
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "meal_name") val mealName: String,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "time") val time: String
)