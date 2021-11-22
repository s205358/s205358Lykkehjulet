package com.example.s205358lykkehjulet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.s205358lykkehjulet.adapter.CategoryListAdapter
import com.example.s205358lykkehjulet.databinding.CategoryListLayoutBinding
import com.example.s205358lykkehjulet.viewmodel.GameViewModel

class ChooseCategoryFragment : Fragment() {

    private var _binding: CategoryListLayoutBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CategoryListLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CategoryListAdapter {
            val action = ChooseCategoryFragmentDirections.actionChooseCategoryFragmentToGameFragment(
                it.name
            )
            findNavController().navigate(action)
        }

        binding.categoryList.layoutManager = LinearLayoutManager(this.context)
        binding.categoryList.adapter = adapter
        adapter.submitList(sharedViewModel.allCategories)
    }
}