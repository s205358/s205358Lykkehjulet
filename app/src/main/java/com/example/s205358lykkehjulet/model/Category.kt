package com.example.s205358lykkehjulet.model

import androidx.annotation.StringRes

data class Category(
    @StringRes val name: Int,
    @StringRes val words: List<Int>
)

fun Category.getRandomWord(): Int {
    return words.random()
}