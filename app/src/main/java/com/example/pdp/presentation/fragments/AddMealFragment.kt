package com.example.pdp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pdp.R
import com.example.pdp.databinding.FragmentAddMealBinding
import com.example.pdp.db.AppDatabase
import com.example.pdp.db.MealEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMealFragment : Fragment(R.layout.fragment_add_meal) {

    private var _binding: FragmentAddMealBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            saveMeal()
        }
    }

    private fun saveMeal() {
        val mealName = binding.editTextMealName.text.toString()
        val calories = binding.editTextCalories.text.toString().toIntOrNull() ?: 0
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