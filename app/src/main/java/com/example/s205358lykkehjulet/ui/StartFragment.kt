package com.example.s205358lykkehjulet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.s205358lykkehjulet.databinding.StartLayoutBinding

class StartFragment : Fragment() {

    private var _binding: StartLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StartLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startAction.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToChooseCategoryFragment()
            findNavController().navigate(action)
        }
    }
}