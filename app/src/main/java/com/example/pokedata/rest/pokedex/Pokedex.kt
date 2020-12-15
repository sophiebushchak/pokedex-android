package com.example.pokedata.rest.pokedex

data class Pokedex (
    val id: Int,
    val name: String,
    val pokemon_entries: List<PokemonEntry>
)