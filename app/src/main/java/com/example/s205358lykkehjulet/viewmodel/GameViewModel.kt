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

    private val _stake = MutableLiveData<Int>()
    val stake: LiveData<Int>
        get() = _stake

    private val _wheelState = MutableLiveData<WheelStates>()
    val wheelState: LiveData<WheelStates>
        get() = _wheelState

    private val _gameState = MutableLiveData<GameStates>()
    val gameState: LiveData<GameStates>
        get() = _gameState

    private val _guessedLetters = mutableListOf<Char>()
    val guessedLetters: List<Char>
        get() = _guessedLetters

    private val _randomWord = MutableLiveData<String>()
    val randomWord: LiveData<String>
        get() = _randomWord

    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int>
        get() = _points

    private val _lives = MutableLiveData<Int>()
    val lives: LiveData<Int>
        get() = _lives

    fun getCategory(name: Int): Category? {
        return allCategories.find {
            it.name == name
        }
    }

    fun setRandomWord(word: String) {
        _randomWord.value = word
    }

    init {
        restart()
    }

    fun getHiddenWord(): String {
        val chars = randomWord.value?.toCharArray()
        val temp = chars?.map {
            it to (guessedLetters.contains(it) || it.isWhitespace() || !it.isLetter())
        }

        var word = ""
        temp?.forEach {
            word += if (it.second) {
                "${it.first} "
            } else {
                "_ "
            }
        }
        return word
    }

    fun restart() {
        _stake.value = 0
        _gameState.value = GameStates.SPIN_WHEEL
        spin() // Random start
        _randomWord.value = ""
        _points.value = NUM_OF_POINTS
        _lives.value = NUM_OF_LIVES
    }

    fun spin() {
        when ((0..36).random()) {
            in 0..4 -> {
                _wheelState.value = WheelStates.EXTRA_TURN
            }
            in 5..7 -> {
                _wheelState.value = WheelStates.MISS_TURN
            }
            in 8..34 -> {
                _wheelState.value = WheelStates.POINTS
            }
            else -> {
                _wheelState.value = WheelStates.BANKRUPTCY
            }
        }
        play()
    }

    fun guess(letter: Char) {
        _guessedLetters.add(letter)

        val occurrences = letter.toString().count {
            randomWord.value!!.contains(letter, ignoreCase = true)
        }

        if (occurrences > 0) {
            val winnings = occurrences.times(stake.value!!)
            _points.value = (_points.value)?.plus(winnings)
        } else {
            _lives.value = (_lives.value)?.minus(1)
        }

        _gameState.value = GameStates.SPIN_WHEEL
    }

    private fun play() {
        when(wheelState.value) {
            WheelStates.EXTRA_TURN -> {
                _lives.value = (_lives.value)?.plus(1)
                _gameState.value = GameStates.GUESS_LETTER
            }
            WheelStates.MISS_TURN -> {
                _lives.value = (_lives.value)?.minus(1)
                _gameState.value = GameStates.SPIN_WHEEL
            }
            WheelStates.POINTS -> {
                _stake.value = (100..1000).random() // TODO: refactor
                _gameState.value = GameStates.GUESS_LETTER
            }
            WheelStates.BANKRUPTCY -> {
                _lives.value = 0
            }
        }
        if(lives.value?.equals(0) == true) {
            _gameState.value = GameStates.GAME_LOST
        }
    }

    enum class GameStates {
        SPIN_WHEEL,
        GUESS_LETTER,
        GAME_WON,
        GAME_LOST
    }

    enum class WheelStates {
        EXTRA_TURN,
        MISS_TURN,
        BANKRUPTCY,
        POINTS
    }
}