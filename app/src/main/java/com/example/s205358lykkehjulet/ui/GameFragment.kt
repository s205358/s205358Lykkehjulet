package com.example.s205358lykkehjulet.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.s205358lykkehjulet.R
import com.example.s205358lykkehjulet.databinding.GameLayoutBinding
import com.example.s205358lykkehjulet.model.Category
import com.example.s205358lykkehjulet.model.getRandomWord
import com.example.s205358lykkehjulet.viewmodel.GameViewModel
import java.time.Duration

class GameFragment : Fragment() {

    private val navigationArgs: GameFragmentArgs by navArgs()
    private lateinit var gameCategory: Category

    private var _binding: GameLayoutBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GameLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.restart()

        val name = navigationArgs.name
        gameCategory = sharedViewModel.getCategory(name)!!
        val word = resources.getString(gameCategory.getRandomWord())
        sharedViewModel.setRandomWord(word)

        binding.category.setText(gameCategory.name)

        binding.spinAction.setOnClickListener {
            spin()
        }
        binding.guessAction.setOnClickListener {
            guess()
        }
        sharedViewModel.gameState.observe(this.viewLifecycleOwner) {
            binding.spinAction.isEnabled = it.equals(GameViewModel.GameStates.SPIN_WHEEL)
            binding.guessAction.isEnabled = it.equals(GameViewModel.GameStates.GUESS_LETTER)
        }
        sharedViewModel.points.observe(this.viewLifecycleOwner) {
            binding.points.text = it.toString()
        }
        sharedViewModel.lives.observe(this.viewLifecycleOwner) {
            binding.lives.text = it.toString()
        }
        sharedViewModel.stake.observe(this.viewLifecycleOwner) {
            binding.stake.text = it.toString()
        }
    }

    // TODO: Display guessed letters
    // TODO: Validate input
    // TODO: Display hidden word
    // TODO: Possible to win

    private fun spin() {
        sharedViewModel.spin()

        val text = when(sharedViewModel.wheelState.value) {
            GameViewModel.WheelStates.EXTRA_TURN -> getString(R.string.extra_turn)
            GameViewModel.WheelStates.MISS_TURN -> getString(R.string.miss_turn)
            GameViewModel.WheelStates.POINTS -> getString(R.string.try_guess)
            else -> getString(R.string.bankruptcy)
        }
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()

        // TODO: Move else where... should be checked after each action
        when(sharedViewModel.gameState.value) {
            GameViewModel.GameStates.GAME_WON -> {
                val action = GameFragmentDirections.actionGameFragmentToGameWonFragment()
                findNavController().navigate(action)
            }
            GameViewModel.GameStates.GAME_LOST -> {
                val action = GameFragmentDirections.actionGameFragmentToGameLostFragment()
                findNavController().navigate(action)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun guess() {
        sharedViewModel.guess(binding.guess.text.toString().toCharArray()[0])
    }
}