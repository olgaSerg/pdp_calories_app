package com.example.pdp.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MealDao {
    @Insert
    fun insert(mealEntry: MealEntry?)

    @Update
    fun update(mealEntry: MealEntry?)

    @Delete
    fun delete(mealEntry: MealEntry?)

    @Query("DELETE FROM meal_table")
    fun deleteAllMeals()

    @get:Query("SELECT * FROM meal_table ORDER BY date DESC")
    val allMeals: LiveData<List<MealEntry>>?

    @Query("SELECT * FROM meal_table WHERE id = :mealId")
    fun getMealById(mealId: Int): LiveData<MealEntry?>?

    @Query("SELECT * FROM meal_table ORDER BY date DESC")
    fun allMealsPagingSource(): PagingSource<Int, MealEntry>
}
