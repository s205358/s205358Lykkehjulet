package com.example.s205358lykkehjulet.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.s205358lykkehjulet.databinding.GameLayoutBinding
import com.example.s205358lykkehjulet.model.Category
import com.example.s205358lykkehjulet.model.getRandomWord
import com.example.s205358lykkehjulet.viewmodel.GameViewModel

class GameFragment : Fragment() {

    private val navigationArgs: GameFragmentArgs by navArgs()
    private lateinit var category: Category

    private var _binding: GameLayoutBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GameViewModel by activityViewModels()

    /**
     * Binds [GameFragment] to game_layout.xml using the auto-generated [GameLayoutBinding] via View Binding
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GameLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Needs to be refactored
        val name = navigationArgs.name
        category = sharedViewModel.getCategory(name)!!
        val word = resources.getString(category.getRandomWord())
        sharedViewModel.setRandomWord(word)

        /**
         * Player should not be able to guess before he/she have spun the wheel
         */
        binding.apply {
            guessAction.isEnabled = false
        }
        /**
         * Initialize the UI
         */
        bind()
    }

    private fun spinWheel() {
        /**
         * Call [GameViewModel] to update state
         */
        sharedViewModel.spinWheel()

        when(sharedViewModel.gameState.value) {
            /**
             * Player must guess a letter:
             * Disable ability to spin wheel.
             * Enable ability to guess letter.
             * Update UI for possible changes.
             */
            GameViewModel.GameStatus.GUESS_LETTER -> {
                binding.spinTheWheel.isEnabled = false
                binding.guessAction.isEnabled = true
                bind()
            }
            /**
             * Player has won and is navigated to [GameWonFragment]
             */
            GameViewModel.GameStatus.GAME_WON -> {
                val action = GameFragmentDirections.actionGameFragmentToGameWonFragment()
                findNavController().navigate(action)
            }
            /**
             * Player has lost and is navigated to [GameLostFragment]
             */
            GameViewModel.GameStatus.GAME_LOST -> {
                val action = GameFragmentDirections.actionGameFragmentToGameLostFragment()
                findNavController().navigate(action)
            }
            /**
             * Player should spin the wheel, update UI for possible changes and do nothing.
             */
            else -> {
                bind()
            }
        }
    }

    private fun guessLetter() {
        // TODO: Validate input throw error if missing...
        sharedViewModel.guessLetter('R')
        binding.spinTheWheel.isEnabled = true
        binding.guessAction.isEnabled = false
        bind()
        TODO()
    }

    private fun bind() {
        binding.apply {
            categoryName.setText(category.name)
            wordToGuess.text = sharedViewModel.randomWord.value.toString()
            lives.text = sharedViewModel.lives.value.toString()
            points.text = sharedViewModel.points.value.toString()
            pointsToWin.text = sharedViewModel.wheelPoints.value.toString()

            spinTheWheel.setOnClickListener {
                spinWheel()
            }

            guessAction.setOnClickListener {
                guessLetter()
            }
        }
    }
}