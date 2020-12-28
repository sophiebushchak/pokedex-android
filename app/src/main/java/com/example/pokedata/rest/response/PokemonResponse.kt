package com.example.pokedata.rest.response

import com.example.pokedata.models.PokemonDetailed

data class PokemonResponse(
    val message: String,
    val statusCode: Int,
    val pokemon: PokemonDetailed
)