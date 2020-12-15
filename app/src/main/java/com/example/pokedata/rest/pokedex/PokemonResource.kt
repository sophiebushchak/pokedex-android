package com.example.pokedata.rest.pokedex

import com.example.pokedata.rest.utility.NamedApiResource

data class PokemonResource(
        val id: Int,
        val name: String,
        val height: Int,
        val order: Int,
        val weight: Int,
        val forms: List<NamedApiResource>,
        val sprites: PokemonSprites,
        val species: NamedApiResource,
        val types: List<PokemonTypeResource>
)

