package com.example.pdp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import com.example.pdp.R
import com.example.pdp.db.AppDatabase
import com.example.pdp.db.MealEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMealFragment : Fragment(R.layout.fragment_add_meal) {

    private var timePicker: TimePicker? = null
    private var mealNameEditText: EditText? = null
    private var caloriesEditText: EditText? = null
    private var saveButton: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timePicker = view.findViewById(R.id.time_picker)
        mealNameEditText = view.findViewById(R.id.edit_text_meal_name)
        caloriesEditText = view.findViewById(R.id.edit_text_calories)
        saveButton = view.findViewById(R.id.button_save)

        saveButton?.setOnClickListener {
            saveMeal()
        }
    }
    private fun saveMeal() {
        val mealName = mealNameEditText?.text.toString()
        val calories = caloriesEditText?.text.toString().toIntOrNull() ?: 0
        val date = Date()
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

        val mealEntry = MealEntry(
            mealName = mealName,
            calories = calories,
            date = date,
            time = time
        )

        val db = AppDatabase.getDatabase(requireActivity().applicationContext)
        val mealDao = db.mealDao()

        Thread {
            mealDao.insert(mealEntry)
            requireActivity().runOnUiThread {
                findNavController().popBackStack()
            }
        }.start()
    }
}