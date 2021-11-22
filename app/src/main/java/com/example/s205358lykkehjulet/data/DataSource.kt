package com.example.s205358lykkehjulet.data

import com.example.s205358lykkehjulet.R
import com.example.s205358lykkehjulet.model.Category

class DataSource {
    fun loadCategoriesWithWords(): List<Category> {
        return listOf(
            Category(
                name = R.string.category1,
                words = listOf(
                    R.string.category1_word1,
                    R.string.category1_word2,
                    R.string.category1_word3,
                    R.string.category1_word4,
                    R.string.category1_word5,
                    R.string.category1_word6,
                    R.string.category1_word7
                )
            ),
            Category(
                name = R.string.category2,
                words = listOf(
                    R.string.category2_word1,
                    R.string.category2_word2,
                    R.string.category2_word3,
                    R.string.category2_word4,
                    R.string.category2_word5,
                    R.string.category2_word6,
                    R.string.category2_word7
                )
            ),
            Category(
                name = R.string.category3,
                words = listOf(
                    R.string.category3_word1,
                    R.string.category3_word2,
                    R.string.category3_word3,
                    R.string.category3_word4,
                    R.string.category3_word5,
                    R.string.category3_word6,
                    R.string.category3_word7
                )
            ),
        )
    }
}

/*
    <!--
    <string name="category2_word1">Robert De Niro</string>
    <string name="category2_word2">Al Pacino</string>
    <string name="category2_word3">Joe Pesci</string>
    <string name="category2_word4">Leonardo DiCaprio</string>
    <string name="category2_word5">Ray Liotta</string>
    <string name="category2_word6">Robert Duvall</string>
    <string name="category2_word7">Matthew McConaughey</string>
    -->
 */