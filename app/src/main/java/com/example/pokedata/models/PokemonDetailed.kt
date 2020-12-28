package com.example.pokedata.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class PokemonDetailed(
    val pokedexNumber: Int,
    val pokemonName: String,
    val primaryType: String,
    val secondaryType: String?,
    val sprites: @RawValue PokemonBasic.PokemonSprites,
    val genus: String,
    val generation: Int,
    val pokedexEntryDescription: String,
    val height: Int,
    val weight: Int,
    val color: String,
)