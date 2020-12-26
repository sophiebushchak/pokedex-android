package com.example.pokedata.rest

import android.content.Context
import com.example.pokedata.models.PokemonBasic
import kotlinx.coroutines.withTimeout

class PokeApiRepository(context: Context) {

    private val pokeApiService: PokeApiService = PokeApi(context).createApi()

    suspend fun getPokemonPaginated(offset: Int, limit: Int): List<PokemonBasic> {
        try {
            val pokemon = mutableListOf<PokemonBasic>()
            val response = withTimeout(5_000) {
                pokeApiService.getPokedexPaginated(offset, limit)
            }
            if (response.pokemon.isEmpty()) {
                throw Error("Received empty results.")
            }
            pokemon.addAll(response.pokemon)
            return pokemon
        } catch (error: Throwable) {
            var message = "Something went wrong while retrieving Pokemon."
            error.message?.let {
                message = it
            }
            throw PokeApiError(message, error)
        }
    }

    suspend fun getTotalPokemon(): Int {
        try {
            val response = withTimeout(5_000) {
                pokeApiService.getPokedexTotalPokemon()
            }
            return response.count
        } catch (error: Throwable) {
            var message = "Something went wrong while retrieving total Pokemon."
            error.message?.let {
                message = it
            }
            throw PokeApiError(message, error)
        }
    }

    class PokeApiError(message: String, cause: Throwable) : Throwable(message, cause)
}