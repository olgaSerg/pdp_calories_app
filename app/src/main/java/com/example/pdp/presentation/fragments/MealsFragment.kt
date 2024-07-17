package com.example.pdp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdp.presentation.adapters.MealAdapter
import com.example.pdp.R
import com.example.pdp.databinding.FragmentMealsBinding
import com.example.pdp.db.AppDatabase
import com.example.pdp.db.MealDao
import com.example.pdp.db.MealEntry
import com.example.pdp.presentation.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MEAL_ID_KEY = "mealId"

class MealsFragment : BaseFragment<FragmentMealsBinding>() {

    private var mealAdapter: MealAdapter? = null
    private var mealDao: MealDao? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMealsBinding {
        return FragmentMealsBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mealDao = AppDatabase.getDatabase(requireContext()).mealDao()
        setupRecyclerView()
        setClickListener()
        observeMeals()
    }

    private fun setupRecyclerView() {
        mealAdapter = MealAdapter(
            onDeleteClick = { meal ->
                deleteMeal(meal)
            },
            onUpdateClick = { meal ->
                navigateToUpdateMeal(meal)
            }
        )
        binding.rvMealEntries.adapter = mealAdapter
        binding.rvMealEntries.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun observeMeals() {
        mealDao?.let { dao ->
            val pager = Pager(
                config = PagingConfig(
                    pageSize = 5,
                    maxSize = 7,
                    initialLoadSize = 7,
                    prefetchDistance = 1
                ),
                pagingSourceFactory = { dao.allMealsPagingSource() }
            ).liveData

            pager.observe(viewLifecycleOwner) {
                mealAdapter?.submitData(lifecycle, it)
            }
        }
    }


    private fun deleteMeal(meal: MealEntry) {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mealDao?.delete(meal)
            }
            mealAdapter?.refresh()
        }
    }

    private fun navigateToUpdateMeal(meal: MealEntry) {
        val bundle = Bundle().apply {
            putInt(MEAL_ID_KEY, meal.id)
        }
        findNavController().navigate(R.id.action_meals_fragment_to_add_meal_fragment, bundle)
    }

    private fun setClickListener() {
        binding.fabAddMeal.setOnClickListener {
            findNavController().navigate(R.id.action_meals_fragment_to_add_meal_fragment)
        }
    }

    override fun onDestroyView() {
        mealAdapter = null
        super.onDestroyView()
    }
}