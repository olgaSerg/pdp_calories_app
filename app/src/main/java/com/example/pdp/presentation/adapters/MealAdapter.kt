package com.example.pdp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.pdp.databinding.ItemMealBinding
import com.example.pdp.db.MealEntry

class MealAdapter(
    private val onDeleteClick: (MealEntry) -> Unit,
    private val onUpdateClick: (MealEntry) -> Unit
) : PagingDataAdapter<MealEntry, MealAdapter.MealViewHolder>(MEAL_COMPARATOR) {

    class MealViewHolder(
        private val binding: ItemMealBinding,
        private val onDeleteClick: (MealEntry) -> Unit,
        private val onUpdateClick: (MealEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: MealEntry) {
            binding.tvMealName.text = meal.mealName
            binding.tvMealTime.text = meal.time
            binding.tvMealCalories.text = meal.calories.toString()
            binding.icDelete.setOnClickListener {
                onDeleteClick(meal)
            }
            binding.icEdit.setOnClickListener {
                onUpdateClick(meal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding, onDeleteClick, onUpdateClick)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = getItem(position)
        meal?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val MEAL_COMPARATOR = object : DiffUtil.ItemCallback<MealEntry>() {
            override fun areItemsTheSame(oldItem: MealEntry, newItem: MealEntry): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MealEntry, newItem: MealEntry): Boolean {
                return oldItem == newItem
            }
        }
    }
}