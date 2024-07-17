package com.example.pdp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pdp.databinding.FragmentAddMealBinding
import com.example.pdp.db.AppDatabase
import com.example.pdp.db.MealEntry
import com.example.pdp.presentation.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val MEAL_ID_KEY = "mealId"

class AddMealFragment : BaseFragment<FragmentAddMealBinding>() {

    private var mealId: Int = -1

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddMealBinding {
        return FragmentAddMealBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mealId = arguments?.getInt(MEAL_ID_KEY, -1) ?: -1

        loadMealData()

        binding.buttonSave.setOnClickListener {
            saveMeal()
        }
    }

    private fun loadMealData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireActivity().applicationContext)
            val mealDao = db.mealDao()

            if (mealId != -1) {
                val existingMeal = withContext(Dispatchers.IO) {
                    mealDao.findById(mealId)
                }

                existingMeal?.let {
                    updateUIWithMeal(it)
                }
            }
        }
    }

    private fun updateUIWithMeal(meal: MealEntry) {
        binding.editTextCalories.setText(meal.calories.toString())
        binding.editTextMealName.setText(meal.mealName)
    }

    private fun saveMeal() {
        val mealName = binding.editTextMealName.text.toString()
        val calories = binding.editTextCalories.text.toString().toIntOrNull() ?: 0
        val date = LocalDate.now()
        val time = formatTime()

        val mealEntry = MealEntry(
            mealName = mealName,
            calories = calories,
            date = date,
            time = time
        )

        viewLifecycleOwner.lifecycleScope.launch {
            saveMealToDatabase(mealEntry)
            findNavController().popBackStack()
        }
    }

    private suspend fun saveMealToDatabase(mealEntry: MealEntry) {
        val db = AppDatabase.getDatabase(requireActivity().applicationContext)
        val mealDao = db.mealDao()

        withContext(Dispatchers.IO) {
            val existingMeal = mealDao.findById(mealId)
            if (existingMeal != null) {
                existingMeal.apply {
                    mealName = mealEntry.mealName
                    calories = mealEntry.calories
                    date = mealEntry.date
                    time = mealEntry.time
                }
                mealDao.insert(existingMeal)
            } else {
                mealDao.insert(mealEntry)
            }
        }
    }

    private fun formatTime(): String {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}