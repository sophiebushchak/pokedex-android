package com.example.pokedata.rest.pokedex

import com.example.pokedata.rest.utility.NamedApiResource

data class PokemonSpeciesResource (
        val is_baby: Boolean,
        val is_legendary: Boolean,
        val is_mythical: Boolean,
        val generation: NamedApiResource
        )