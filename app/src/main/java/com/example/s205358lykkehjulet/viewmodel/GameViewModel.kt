package com.example.s205358lykkehjulet.viewmodel

import androidx.lifecycle.*
import com.example.s205358lykkehjulet.data.DataSource
import com.example.s205358lykkehjulet.model.Category

private const val NUM_OF_POINTS = 0
private const val NUM_OF_LIVES = 5

class GameViewModel: ViewModel() {

    val allCategories = DataSource().loadCategoriesWithWords()

    private val _stake = MutableLiveData<Int>()
    val stake: LiveData<Int> = _stake

    private val _wheelState = MutableLiveData<WheelState>()
    val wheelState: LiveData<WheelState> = _wheelState

    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    private val _letters = MutableLiveData<MutableList<Char>>()
    val letters: LiveData<List<Char>> = Transformations.map(_letters) {
        it.toList()
    }

    private val _word = MutableLiveData<String>()
    // https://developer.android.com/reference/android/arch/lifecycle/MediatorLiveData
    // https://proandroiddev.com/livedata-transformations-4f120ac046fc
    val word: LiveData<String> = MediatorLiveData<String>().apply {
        fun update() {
            val word = _word.value
            val letters = _letters.value

            value = word?.toCharArray()?.map {
                it to (letters.toString().contains(it, ignoreCase = true) || !it.isLetter())
            }?.map {
                if (it.second) {
                    it.first
                } else {
                    '_'
                }
            }?.joinToString(" ")
        }
        addSource(_word) {
            update()
        }
        addSource(_letters) {
            update()
        }
        update()
    }

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
        _word.value = word
    }

    init {
        restart()
    }

    fun restart() {
        _stake.value = 0
        _gameState.value = GameState.SPIN_WHEEL
        _wheelState.value = null
        _word.value = ""
        _points.value = NUM_OF_POINTS
        _lives.value = NUM_OF_LIVES
        _letters.value = mutableListOf<Char>()
    }

    fun spin() {
        when ((0..36).random()) {
            in 0..4 -> {
                _wheelState.value = WheelState.EXTRA_TURN
            }
            in 5..7 -> {
                _wheelState.value = WheelState.MISS_TURN
            }
            in 8..34 -> {
                _wheelState.value = WheelState.POINTS
            }
            else -> {
                _wheelState.value = WheelState.BANKRUPTCY
            }
        }

        when(wheelState.value) {
            WheelState.EXTRA_TURN -> {
                _stake.value = (100..1000).random()
                _lives.value = (_lives.value)?.plus(1)
                _gameState.value = GameState.GUESS_LETTER
            }
            WheelState.MISS_TURN -> {
                _lives.value = (_lives.value)?.minus(1)
                if(lives.value?.equals(0) == true) {
                    _gameState.value = GameState.GAME_LOST
                } else {
                    _gameState.value = GameState.SPIN_WHEEL
                }
            }
            WheelState.POINTS -> {
                _stake.value = (100..1000).random()
                _gameState.value = GameState.GUESS_LETTER
            }
            WheelState.BANKRUPTCY -> {
                _points.value = 0
            }
        }
    }

    fun guess(letter: Char) {
        val temp = _letters.value ?: mutableListOf()
        temp.add(letter.lowercaseChar())
        _letters.value = temp

        val occurrences = letter.toString().count {
            word.value!!.contains(letter, ignoreCase = true)
        }

        if (occurrences > 0) {
            val winnings = occurrences.times(stake.value!!)
            _points.value = (_points.value)?.plus(winnings)
        } else {
            _lives.value = (_lives.value)?.minus(1)
        }

        if(lives.value?.equals(0) == true) {
            _gameState.value = GameState.GAME_LOST
            return
        }

        val lettersInWord = _word.value?.lowercase()?.trim(' ', '-')?.toList()
        val allFound = (letters.value?.containsAll(lettersInWord!!) == true)

        if (allFound) {
            _gameState.value = GameState.GAME_WON
            return
        }

        _gameState.value = GameState.SPIN_WHEEL
    }

    fun isLetterValid(letter: Char): Boolean {
        return (letters.value?.contains(letter.lowercaseChar()) == false)
    }

    enum class GameState {
        SPIN_WHEEL,
        GUESS_LETTER,
        GAME_WON,
        GAME_LOST
    }

    enum class WheelState {
        EXTRA_TURN,
        MISS_TURN,
        BANKRUPTCY,
        POINTS
    }
}