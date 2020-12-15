package com.example.pokedata.rest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pokedata.models.Pokemon
import com.example.pokedata.models.PokemonType
import kotlinx.coroutines.withTimeout

class PokeApiRepository {
    private val pokeApiService: PokeApiService = PokeApi.createApi()

    val progressCount: LiveData<Int> get() = _progressCount
    private val _progressCount: MutableLiveData<Int> = MutableLiveData()

    suspend fun getAllPokemonForNationalDex(): List<Pokemon> {
        try {
            val pokedex = withTimeout(5_000) {
                pokeApiService.getNationalPokedex()
            }
            if (pokedex.pokemon_entries.isEmpty()) {
                throw Error("Somehow the Pokedex had no Pokemon.")
            }
            val currentPokemonExisting = pokedex.pokemon_entries.size
            val pokemon = mutableListOf<Pokemon>()
            for (i in 1..currentPokemonExisting) {
                val retrievedPokemonResource =  withTimeout(5_000) {
                    pokeApiService.getPokemonById(i)
                }
                val retrievedPokemonSpeciesResource = withTimeout(5_000) {
                    pokeApiService.getPokemonSpeciesByName(retrievedPokemonResource.species.name)
                }
                val types: MutableList<PokemonType> = mutableListOf()
                for (type in retrievedPokemonResource.types) {
                    types.add(PokemonType.valueOf(type.type.name))
                }
                val completePokemon = Pokemon(
                        retrievedPokemonResource.id,
                        retrievedPokemonResource.name,
                        retrievedPokemonResource.height,
                        retrievedPokemonResource.order,
                        retrievedPokemonResource.weight,
                        retrievedPokemonResource.sprites,
                        types,
                        retrievedPokemonSpeciesResource.is_baby,
                        retrievedPokemonSpeciesResource.is_legendary,
                        retrievedPokemonSpeciesResource.is_mythical,
                )
                pokemon.add(completePokemon)
                _progressCount.value = pokemon.size + 1
            }
            return pokemon

        } catch (error: Throwable) {
            var message = "Something went wrong while getting all Pokemon from the National Dex."
            error.message?.let{
                message = it
            }
            throw PokeApiError(message, error)
        }
    }

    class PokeApiError(message: String, cause: Throwable) : Throwable(message, cause)
}