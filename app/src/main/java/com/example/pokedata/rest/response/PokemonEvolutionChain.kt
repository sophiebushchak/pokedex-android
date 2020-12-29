package com.example.pokedata.rest.response

import com.example.pokedata.models.PokemonBasic

data class PokemonEvolutionChain(
    val first: PokemonBasic?,
    val second: PokemonBasic?,
    val third: PokemonBasic?
)