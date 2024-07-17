package com.example.pdp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pdp.R
import com.example.pdp.databinding.FragmentMainBinding
import com.example.pdp.presentation.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.buttonPaging.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_mealsFragment)
        }

        binding.buttonService.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_serviceFragment)
        }

        binding.buttonContacts.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_contactsFragment)
        }
    }
}