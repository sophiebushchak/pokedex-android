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

    enum class PokemonType(val typeColor: Int, val typeBackground: Int) {
        normal(R.color.pokemon_type_normal, R.color.pokemon_type_normal_background),
        fire(R.color.pokemon_type_fire, R.color.pokemon_type_fire_background),
        fighting(R.color.pokemon_type_fighting, R.color.pokemon_type_fighting_background),
        water(R.color.pokemon_type_water, R.color.pokemon_type_water_background),
        flying(R.color.pokemon_type_flying, R.color.pokemon_type_flying_background),
        grass(R.color.pokemon_type_grass, R.color.pokemon_type_grass_background),
        poison(R.color.pokemon_type_poison, R.color.pokemon_type_poison_background),
        electric(R.color.pokemon_type_electric, R.color.pokemon_type_electric_background),
        ground(R.color.pokemon_type_ground, R.color.pokemon_type_ground_background),
        psychic(R.color.pokemon_type_psychic, R.color.pokemon_type_psychic_background),
        rock(R.color.pokemon_type_rock, R.color.pokemon_type_rock_background),
        ice(R.color.pokemon_type_ice, R.color.pokemon_type_ice_background),
        bug(R.color.pokemon_type_bug, R.color.pokemon_type_bug_background),
        dragon(R.color.pokemon_type_dragon, R.color.pokemon_type_dragon_background),
        ghost(R.color.pokemon_type_ghost, R.color.pokemon_type_ghost_background),
        dark(R.color.pokemon_type_dark, R.color.pokemon_type_dark_background),
        steel(R.color.pokemon_type_steel, R.color.pokemon_type_steel_background),
        fairy(R.color.pokemon_type_fairy, R.color.pokemon_type_fairy_background)
    }
}