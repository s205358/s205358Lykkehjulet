package com.example.s205358lykkehjulet.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.s205358lykkehjulet.R
import com.example.s205358lykkehjulet.databinding.GameLostLayoutBinding
import com.example.s205358lykkehjulet.databinding.GameWonLayoutBinding

class GameWonFragment : Fragment() {

    private var _binding: GameWonLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GameWonLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playAgain.setOnClickListener {
            val action = GameWonFragmentDirections.actionGameWonFragmentToStartFragment();
            findNavController().navigate(action)
        }
    }
}