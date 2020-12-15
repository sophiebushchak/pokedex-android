package com.example.pokedata.models

import android.graphics.drawable.Drawable

data class PokemonTest(
        val name: String,
        val pokedexNumber: Int,
        val types: List<String>,
        val image: Int
)
