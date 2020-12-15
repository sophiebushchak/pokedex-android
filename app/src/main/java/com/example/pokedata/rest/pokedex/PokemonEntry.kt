package com.example.pokedata.rest.pokedex

import com.example.pokedata.rest.utility.NamedApiResource

data class PokemonEntry(
        val entry_number: Int,
        val pokemon_species: NamedApiResource
)