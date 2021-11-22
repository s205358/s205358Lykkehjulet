package com.example.s205358lykkehjulet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.s205358lykkehjulet.data.DataSource
import com.example.s205358lykkehjulet.model.Category

private const val NUM_OF_POINTS = 0
private const val NUM_OF_LIVES = 5

class GameViewModel: ViewModel() {

    val allCategories = DataSource().loadCategoriesWithWords();

    private val _wheelPoints = MutableLiveData<Int>()
    val wheelPoints: LiveData<Int> = _wheelPoints

    private val _wheelState = MutableLiveData<WheelStatus>()
    val wheelState: LiveData<WheelStatus> = _wheelState

    private val _gameState = MutableLiveData<GameStatus>()
    val gameState: LiveData<GameStatus> = _gameState

    private val _randomWord = MutableLiveData<String>()
    val randomWord: LiveData<String> = _randomWord

    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> = _points

    private val _lives = MutableLiveData<Int>()
    val lives: LiveData<Int> = _lives

    fun getCategory(name: Int): Category? {
        return allCategories.find {
            it.name == name
        }
    }

    fun setRandomWord(word: String) {
        _randomWord.value = word
    }

    init {
        resetGame()
    }

    private fun resetGame() {
        _wheelPoints.value = 0
        _gameState.value = GameStatus.SPIN_WHEEL
        spinWheel() // Random start
        _randomWord.value = ""
        _points.value = NUM_OF_POINTS
        _lives.value = NUM_OF_LIVES
    }

    fun spinWheel() {
        when ((0..36).random()) {
            in 0..4 -> {
                _wheelState.value = WheelStatus.EXTRA_TURN
            }
            in 5..7 -> {
                _wheelState.value = WheelStatus.MISS_TURN
            }
            in 8..34 -> {
                _wheelState.value = WheelStatus.POINTS
            }
            else -> {
                _wheelState.value = WheelStatus.BANKRUPTCY
            }
        }
        updateGame()
    }

    fun guessLetter(letter: Char) {
        _gameState.value = GameStatus.SPIN_WHEEL
        // TODO
    }

    private fun updateGame() {
        when(wheelState.value) {
            WheelStatus.EXTRA_TURN -> {
                // Player gains a life
                _lives.value?.plus(1)
                // Player guess on letter
                _gameState.value = GameStatus.GUESS_LETTER
            }
            WheelStatus.MISS_TURN -> {
                // Player lose a life
                _lives.value?.minus(1)
                // Player spin the wheel again
                _gameState.value = GameStatus.SPIN_WHEEL
            }
            WheelStatus.POINTS -> {
                // Player get the chance to win a random number points
                _wheelPoints.value = (100..1000).random() // TODO: refactor
                // Player guess on letter
                _gameState.value = GameStatus.GUESS_LETTER
            }
            WheelStatus.BANKRUPTCY -> {
                // Player lose all lives
                _lives.value = 0
            }
        }
    }

    enum class GameStatus {
        SPIN_WHEEL,
        GUESS_LETTER,
        GAME_WON,
        GAME_LOST
    }

    enum class WheelStatus {
        EXTRA_TURN,
        MISS_TURN,
        BANKRUPTCY,
        POINTS
    }
}