package com.example.pokedata.models

import android.graphics.Color
import androidx.core.graphics.toColor
import com.example.pokedata.R

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

    enum class PokemonType(val typeColor: Int) {
        normal(R.color.pokemon_type_normal),
        fire(R.color.pokemon_type_fire),
        fighting(R.color.pokemon_type_fighting),
        water(R.color.pokemon_type_water),
        flying(R.color.pokemon_type_flying),
        grass(R.color.pokemon_type_grass),
        poison(R.color.pokemon_type_poison),
        electric(R.color.pokemon_type_electric),
        ground(R.color.pokemon_type_ground),
        psychic(R.color.pokemon_type_psychic),
        rock(R.color.pokemon_type_rock),
        ice(R.color.pokemon_type_ice),
        bug(R.color.pokemon_type_bug),
        dragon(R.color.pokemon_type_dragon),
        ghost(R.color.pokemon_type_ghost),
        dark(R.color.pokemon_type_dark),
        steel(R.color.pokemon_type_steel),
        fairy(R.color.pokemon_type_fairy)
    }
}