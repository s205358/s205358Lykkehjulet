package com.example.s205358lykkehjulet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.s205358lykkehjulet.data.DataSource

private const val NUM_OF_POINTS = 0
private const val NUM_OF_LIVES = 5

class GameViewModel: ViewModel() {

    val allCategoriesAndWords = DataSource().loadCategoriesWithWords();

    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> = _points

    private val _lives = MutableLiveData<Int>()
    val lives: LiveData<Int> = _lives

    init {
        resetGame()
    }

    private fun resetGame() {
        _points.value = NUM_OF_POINTS
        _lives.value = NUM_OF_LIVES
    }
}