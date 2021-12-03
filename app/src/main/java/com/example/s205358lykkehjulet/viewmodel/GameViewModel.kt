package com.example.s205358lykkehjulet.viewmodel

import androidx.lifecycle.*
import com.example.s205358lykkehjulet.data.DataSource
import com.example.s205358lykkehjulet.model.Category

private const val NUM_OF_POINTS = 0
private const val NUM_OF_LIVES = 5

class GameViewModel : ViewModel() {

    // Load the categories
    val allCategories = DataSource().loadCategoriesWithWords()

    // Set/get category
    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> = _category

    // Set/get points at stake
    private val _stake = MutableLiveData<Int>()
    val stake: LiveData<Int> = _stake

    // Set/get state of wheel
    private val _wheelState = MutableLiveData<WheelState>()
    val wheelState: LiveData<WheelState> = _wheelState

    // Set/get state of game
    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    // Set/get list of words
    private val _letters = MutableLiveData<MutableList<Char>>()
    val letters: LiveData<List<Char>> = Transformations.map(_letters) {
        it.toList()
    }

    // Get/set hidden word
    private val _word = MutableLiveData<String>()

    // To "transform" the word, use MediatorLiveData as word transformation is dependent on both word and letters
    // https://developer.android.com/reference/android/arch/lifecycle/MediatorLiveData
    val word: LiveData<String> = MediatorLiveData<String>().apply {
        // "Transform" hidden word to hide letters that the player haven't been guessed
        fun update() {
            val word = _word.value
            val letters = _letters.value

            // Convert word to chars
            // Maps char to pair of char and boolean
            // If char is contained in list of letters the player have guessed set true
            // Else false
            value = word?.toCharArray()?.map {
                it to (letters.toString().contains(it, ignoreCase = true) || !it.isLetter())
            }?.map {
                // If letter is present show it
                // Else hide it
                if (it.second) {
                    it.first
                } else {
                    '_'
                }
                // Return the letters as string with join using " "
            }?.joinToString(" ")
        }
        // If word update, notify about the change and run update()
        addSource(_word) {
            update()
        }
        // If word letters, notify about the change and run update()
        addSource(_letters) {
            update()
        }
        // Run update on init
        update()
    }

    // Set/get points
    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> = _points

    // Set/get lives
    private val _lives = MutableLiveData<Int>()
    val lives: LiveData<Int> = _lives

    // set category by StringRessourceId
    fun setCategory(name: Int) {
        _category.value = allCategories.find {
            it.name == name
        }
    }

    // Set random word
    fun setWord(word: String) {
        _word.value = word
    }

    // Reset data on init
    init {
        restart()
    }

    // Reset values
    fun restart() {
        _stake.value = 0
        _gameState.value = GameState.SPIN_WHEEL
        _wheelState.value = null
        _word.value = ""
        _points.value = NUM_OF_POINTS
        _lives.value = NUM_OF_LIVES
        _letters.value = mutableListOf<Char>()
    }

    // Called upon wheel spin
    fun spin() {
        // Random number between 0 and 36
        when ((0..36).random()) {
            // If the number is between 0 and 4, extra turn
            in 0..4 -> {
                _wheelState.value = WheelState.EXTRA_TURN
            }
            // If the number is between 5 and 7, miss turn
            in 5..7 -> {
                _wheelState.value = WheelState.MISS_TURN
            }
            // If the number is between 8 and 34, chance of winning points
            in 8..34 -> {
                _wheelState.value = WheelState.POINTS
            }
            // Else go bankrupt
            else -> {
                _wheelState.value = WheelState.BANKRUPTCY
            }
        }

        // Act on wheel state
        when (wheelState.value) {
            // If extra turn, set stake to random between 100 and 1000, add a live and set game state to guess letter
            WheelState.EXTRA_TURN -> {
                _stake.value = (100..1000).random()
                _lives.value = (_lives.value)?.plus(1)
                _gameState.value = GameState.GUESS_LETTER
            }
            // If miss turn, take a live, check if lives left and if so set game state to spin wheel else to game lost
            WheelState.MISS_TURN -> {
                _lives.value = (_lives.value)?.minus(1)
                if (lives.value?.equals(0) == true) {
                    _gameState.value = GameState.GAME_LOST
                } else {
                    _gameState.value = GameState.SPIN_WHEEL
                }
            }
            // If points, set the stake to random between 100 and 1000 and set game state to guess letter
            WheelState.POINTS -> {
                _stake.value = (100..1000).random()
                _gameState.value = GameState.GUESS_LETTER
            }
            // If bankrupt, reset points
            WheelState.BANKRUPTCY -> {
                _points.value = 0
            }
        }
    }

    // Call upon letter guess
    fun guess(letter: Char) {
        // Update letters so that observer is notified
        val temp = _letters.value ?: mutableListOf()
        temp.add(letter.lowercaseChar())
        _letters.value = temp

        // Count occurences of letter in hidden word
        val occurrences = letter.toString().count {
            word.value!!.contains(letter, ignoreCase = true)
        }

        // If there are any occurences, add stake times occurences to points else take a live
        if (occurrences > 0) {
            val winnings = occurrences.times(stake.value!!)
            _points.value = (_points.value)?.plus(winnings)
        } else {
            _lives.value = (_lives.value)?.minus(1)
        }

        // If all lives lost, game lost
        if (lives.value?.equals(0) == true) {
            _gameState.value = GameState.GAME_LOST
            return
        }


        // val lettersInWord = _word.value?.lowercase()?.trim(' ', '-')?.toList()
        // val allFound = (letters.value?.containsAll(lettersInWord!!) == true)

        // Check if letters guessed contains all letters in word by santizing those and converting them to list
        if (letters.value?.containsAll(_word.value?.lowercase()?.filterNot {
                (it == ' ') || (it == '-')
            }?.toList()!!) == true) {
            _gameState.value = GameState.GAME_WON
            return
        }

        _gameState.value = GameState.SPIN_WHEEL
    }

    fun isLetterValid(letter: String): Boolean {
        if (letter.isNotEmpty() || letter.length == 1) {
            return (letters.value?.contains(letter.single()) == false && letter.single().isLetter())
        }
        return false
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