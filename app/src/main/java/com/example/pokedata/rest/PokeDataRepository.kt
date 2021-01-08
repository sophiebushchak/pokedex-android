package com.example.pokedata.rest

import android.content.Context
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.response.PokemonEvolutionChain
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

/**
 * Uses the [PokeDataService] to get various data through HTTP.
 */
class PokeDataRepository(context: Context) {

    private val pokeDataService: PokeDataService = PokeDataApi(context).createApi()

    /**
     * Takes an offset and a limit in order to paginate.
     */
    suspend fun getPokemonPaginated(offset: Int, limit: Int): List<PokemonBasic> {
        try {
            val pokemon = mutableListOf<PokemonBasic>()
            val response = withTimeout(5_000) {
                pokeDataService.getPokedexPaginated(offset, limit)
            }
            if (response.pokemon.isEmpty()) {
                throw Error("Received empty results.")
            }
            pokemon.addAll(response.pokemon)
            return pokemon
        } catch (timedOutException: TimeoutCancellationException) {
            throw handleTimedOutException(timedOutException)
        } catch (error: Throwable) {
            var message = "Something went wrong while retrieving Pokemon."
            error.message?.let {
                message = it
            }
            throw PokeDataRepositoryException(message, error)
        }
    }

    /**
     * Gets the total number of Pokemon, this is primarily used to know when to stop paginating.
     */
    suspend fun getTotalPokemon(): Int {
        try {
            val response = withTimeout(5_000) {
                pokeDataService.getPokedexTotalPokemon()
            }
            return response.count
        } catch (timedOutException: TimeoutCancellationException) {
            throw handleTimedOutException(timedOutException)
        } catch (error: Throwable) {
            var message = "Something went wrong while retrieving total Pokemon."
            error.message?.let {
                message = it
            }
            throw PokeDataRepositoryException(message, error)
        }
    }

    /**
     * Search for a Pokemon by its name. This matches the string loosely for proper searhing.
     */
    suspend fun getPokemonWithSearch(nameSearchString: String): List<PokemonBasic> {
        try {
            val pokemon = mutableListOf<PokemonBasic>()
            val response = withTimeout(5_000) {
                pokeDataService.getPokedexWithSearch(nameSearchString)
            }
            if (response.pokemon.isEmpty()) {
                throw Error("Could not find any Pokemon with $nameSearchString in name.")
            }
            pokemon.addAll(response.pokemon)
            return pokemon
        } catch (timedOutException: TimeoutCancellationException) {
            throw handleTimedOutException(timedOutException)
        } catch (error: Throwable) {
            var message = "Something went wrong during search."
            error.message?.let {
                message = it
            }
            throw PokeDataRepositoryException(message, error)
        }
    }

    /**
     * Get a particular Pokemon by its Pokedex number.
     */
    suspend fun getPokemonByPokedexNumber(pokedexNumber: Int): PokemonDetailed {
        try {
            val response = withTimeout(5_000) {
                pokeDataService.getPokemonByPokedexNumber(pokedexNumber)
            }
            if (response.statusCode == 404 || response.pokemon == null) {
                throw Error(response.message)
            }
            return response.pokemon
        } catch (timedOutException: TimeoutCancellationException) {
            throw handleTimedOutException(timedOutException)
        } catch (error: Throwable) {
            var message = "Something went wrong while getting Pokemon by Pokedex number"
            error.message?.let {
                message = it
            }
            throw PokeDataRepositoryException(message, error)
        }
    }

    /**
     * Gets a particular Pokemon's evolution chain.
     */
    suspend fun getPokemonEvolutionChain(pokedexNumber: Int): PokemonEvolutionChain {
        try {
            return withTimeout(5_000) {
                pokeDataService.getPokemonEvolutionChain(pokedexNumber)
            }
        } catch (timedOutException: TimeoutCancellationException) {
            throw handleTimedOutException(timedOutException)
        } catch (error: Throwable) {
            var message = "Could not retrieve evolution chain."
            error.message?.let {
                message = it
            }
            throw PokeDataRepositoryException(message, error)
        }
    }

    private fun handleTimedOutException(exception: TimeoutCancellationException): PokeDataRepositoryException {
        return PokeDataRepositoryException(
            "Could not connect to the server. Please check your internet connection.",
            exception
        )
    }

    class PokeDataRepositoryException(message: String, cause: Throwable) : Throwable(message, cause)
}