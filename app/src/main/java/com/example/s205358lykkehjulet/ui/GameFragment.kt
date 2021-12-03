package com.example.s205358lykkehjulet.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.s205358lykkehjulet.R
import com.example.s205358lykkehjulet.adapter.LetterListAdapter
import com.example.s205358lykkehjulet.databinding.GameLayoutBinding
import com.example.s205358lykkehjulet.model.getRandomWord
import com.example.s205358lykkehjulet.viewmodel.GameViewModel

class GameFragment : Fragment() {

    private val navigationArgs: GameFragmentArgs by navArgs()

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

        sharedViewModel.setCategory(navigationArgs.name)
        sharedViewModel.setWord(resources.getString(sharedViewModel.category.value?.getRandomWord()!!))

        binding.letterList.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        sharedViewModel.letters.observe(this.viewLifecycleOwner) {
            val adapter = LetterListAdapter(it)
            binding.letterList.adapter = adapter
        }

        binding.category.setText(sharedViewModel.category.value?.name!!)

        binding.spinAction.setOnClickListener {
            spin()
        }
        binding.guessAction.setOnClickListener {
            guess()
        }
        binding.backToStart.setOnClickListener {
            val action = GameFragmentDirections.actionGameFragmentToStartFragment()
            findNavController().navigate(action)
        }
        sharedViewModel.gameState.observe(this.viewLifecycleOwner) {
            binding.spinAction.isEnabled = it.equals(GameViewModel.GameState.SPIN_WHEEL)
            binding.guessAction.isEnabled = it.equals(GameViewModel.GameState.GUESS_LETTER)
        }
        sharedViewModel.points.observe(this.viewLifecycleOwner) {
            binding.points.text = getString(R.string.points, it.toString())
        }
        sharedViewModel.lives.observe(this.viewLifecycleOwner) {
            binding.lives.text = getString(R.string.lives, it.toString())
        }
        sharedViewModel.stake.observe(this.viewLifecycleOwner) {
            binding.stake.text = getString(R.string.stake, it.toString())
        }
        sharedViewModel.word.observe(this.viewLifecycleOwner) {
            binding.word.text = it.toString()
        }
    }

    private fun spin() {
        sharedViewModel.spin()
        val text = when (sharedViewModel.wheelState.value) {
            GameViewModel.WheelState.EXTRA_TURN -> getString(R.string.extra_turn)
            GameViewModel.WheelState.MISS_TURN -> getString(R.string.miss_turn)
            GameViewModel.WheelState.POINTS -> getString(R.string.try_guess)
            else -> getString(R.string.bankruptcy)
        }
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
        update()
    }

    private fun guess() {
        val input = (binding.guess.text?.toString() ?: " ")
        if (sharedViewModel.isLetterValid(input)) {
            sharedViewModel.guess(input.single())
        } else {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.error))
            builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
        update()
    }

    private fun update() {
        when (sharedViewModel.gameState.value) {
            GameViewModel.GameState.GAME_WON -> {
                val action = GameFragmentDirections.actionGameFragmentToGameWonFragment()
                findNavController().navigate(action)
            }
            GameViewModel.GameState.GAME_LOST -> {
                val action = GameFragmentDirections.actionGameFragmentToGameLostFragment()
                findNavController().navigate(action)
            }
        }
    }
}