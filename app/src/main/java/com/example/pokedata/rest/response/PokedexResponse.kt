package com.example.pokedata.rest.response

import com.example.pokedata.models.PokemonBasic

data class PokedexResponse(
        val message: String,
        val statusCode: Int,
        val pokemon: List<PokemonBasic>
)