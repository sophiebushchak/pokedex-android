package com.example.pokedata.models

import com.example.pokedata.rest.pokedex.PokemonResource
import com.example.pokedata.rest.pokedex.PokemonSprites
import com.example.pokedata.rest.pokedex.PokemonTypeResource
import com.example.pokedata.rest.utility.NamedApiResource

data class Pokemon (
        val id: Int,
        val name: String,
        val height: Int,
        val order: Int,
        val weight: Int,
        val sprites: PokemonSprites,
        val types: List<PokemonType>,
        val isBaby: Boolean,
        val isLegendary: Boolean,
        val isMythical: Boolean,
)