package com.example.pokedata.rest.pokedex

import com.example.pokedata.rest.utility.NamedApiResource

data class PokemonEntryResource(
        val entry_number: Int,
        val pokemon_species: NamedApiResource
)