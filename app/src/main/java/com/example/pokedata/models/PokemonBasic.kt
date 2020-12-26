package com.example.pokedata.models

open class PokemonBasic(
        val pokedexNumber: Int,
        val pokemonName: String,
        val primaryType: String,
        val secondaryType: String?,
        val sprites: PokemonSprites
) {

    data class PokemonSprites(
            val front: String,
            val back: String,
            val frontShiny: String,
            val backShiny: String,
    )

    enum class PokemonType {
        normal,
        fire,
        fighting,
        water,
        flying,
        grass,
        poison,
        electric,
        ground,
        psychic,
        rock,
        ice,
        bug,
        dragon,
        ghost,
        dark,
        steel,
        fairy
    }
}