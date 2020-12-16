package com.example.pokedata.rest

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pokedata.rest.pokedex.PokemonResource
import kotlinx.coroutines.withTimeout

class PokeApiRepository(context: Context) {

    private val pokeApiService: PokeApiService = PokeApi(context).createApi()

    suspend fun getPokemonLimit(): Int {
        try {
            return withTimeout(5_000) {
                pokeApiService.getNationalPokedex().pokemon_entries.size
            }
        } catch (error: Throwable) {
            var message = "Something went wrong while counting Pokemon from the National Dex."
            error.message?.let {
                message = it
            }
            throw PokeApiError(message, error)
        }
    }

    suspend fun getPokemonPaginated(limit: Int, offset: Int): List<PokemonResource> {
        try {
            val pokemonRetrieved = mutableListOf<PokemonResource>()
            for (i in offset+1..limit) {
                val pokemon = withTimeout(5_000) { pokeApiService.getPokemonById(i) }
                pokemonRetrieved.add(pokemon)
            }
            return pokemonRetrieved
        } catch (error: Throwable) {
            var message = "Something went wrong while counting Pokemon from the National Dex."
            error.message?.let {
                message = it
            }
            throw PokeApiError(message, error)
        }
    }

    class PokeApiError(message: String, cause: Throwable) : Throwable(message, cause)
}