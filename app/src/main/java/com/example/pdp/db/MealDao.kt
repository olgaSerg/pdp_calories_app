package com.example.pdp.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mealEntry: MealEntry?)

    @Delete
    fun delete(mealEntry: MealEntry?)

    @Query("SELECT * FROM meal_table WHERE id = :id LIMIT 1")
    fun findById(id: Int): MealEntry?

    @Query("SELECT * FROM meal_table ORDER BY date DESC")
    fun allMealsPagingSource(): PagingSource<Int, MealEntry>
}
