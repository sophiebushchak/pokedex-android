package com.example.pokedata.rest.pokedex

import com.example.pokedata.rest.utility.NamedApiResource

data class PokemonTypeResource(
        val slot: Int,
        val type: NamedApiResource
)