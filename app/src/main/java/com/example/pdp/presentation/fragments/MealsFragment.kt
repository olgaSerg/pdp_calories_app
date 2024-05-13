package com.example.pdp.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdp.presentation.adapters.MealAdapter
import com.example.pdp.app.MealApp
import com.example.pdp.R
import com.example.pdp.databinding.FragmentMealsBinding

class MealsFragment : Fragment() {

    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mealAdapter: MealAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mealAdapter = MealAdapter()
        binding.rvMealEntries.adapter = mealAdapter
        binding.rvMealEntries.layoutManager = LinearLayoutManager(requireActivity())

        setClickListener()

        val mealDao = MealApp.db?.mealDao() ?: return

        val pager = Pager(
            config = PagingConfig(
                pageSize = 5,
                maxSize = 7,
                initialLoadSize = 7,
                prefetchDistance = 1
            ),
            pagingSourceFactory = { mealDao.allMealsPagingSource() }
        ).liveData

        pager.observe(viewLifecycleOwner) {
            mealAdapter.submitData(lifecycle, it)
        }

        mealAdapter.addLoadStateListener { loadState ->
            handleLoadStates(loadState)
        }
    }

    private fun handleLoadStates(loadState: CombinedLoadStates) {
        if (loadState.refresh is LoadState.Loading) {
            Log.d("PagingLog", "Initial load starts.")
        }

        if (loadState.append is LoadState.Loading) {
            Log.d("PagingLog", "Loading next page.")
        }

        if (loadState.append is LoadState.NotLoading && loadState.append.endOfPaginationReached) {
            Log.d("PagingLog", "End of data reached.")
        }

        if (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached) {
            if (mealAdapter.itemCount < 1) {
                Log.d("PagingLog", "No items to display.")
            }
        }
    }

    private fun setClickListener() {
        binding.fabAddMeal.setOnClickListener {
            findNavController().navigate(R.id.action_meals_fragment_to_add_meal_fragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}