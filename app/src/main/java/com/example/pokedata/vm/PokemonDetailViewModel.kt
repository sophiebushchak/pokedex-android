package com.example.pokedata.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.firebase.FavouritesRepository
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.PokeDataRepository
import com.example.pokedata.rest.response.PokemonEvolutionChain
import kotlinx.coroutines.launch

/**
 * ViewModel for the [PokemonDetailFragment]. It has functions related to getting specific data
 * about a particular Pokemon.
 */
class PokemonDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val pokeApiRepository = PokeDataRepository(application.applicationContext)
    private val favouritesRepository = FavouritesRepository()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    val favouriteStatus: LiveData<Pair<String, Boolean>?> get() = favouritesRepository.favouriteStatus
    val favourited: LiveData<Pair<String, Boolean>?> get() = favouritesRepository.favourited

    private val _currentPokemon = MutableLiveData<PokemonDetailed?>()
    val currentPokemon: LiveData<PokemonDetailed?> get() = _currentPokemon

    private val _currentEvolutionChain = MutableLiveData<PokemonEvolutionChain?>()
    val currentEvolutionChain: LiveData<PokemonEvolutionChain?> get() = _currentEvolutionChain

    /**
     * Get information a particular Pokemon by its Pokedex number and emit as a LiveData value.
     */
    fun getPokemonDetailed(pokedexNumber: Int) {
        viewModelScope.launch {
            try {
                val pokemon = pokeApiRepository.getPokemonByPokedexNumber(pokedexNumber)
                println(pokemon)
                _currentPokemon.value = pokemon
                _currentPokemon.value = null
            } catch (error: PokeDataRepository.PokeDataRepositoryException) {
                println(error)
                notifyError(error.message)
            }
        }
    }

    /**
     * Get the favourited status of a Pokemon. No LiveData value is emitted directly here, but
     * the [FavouritesRepository] will emit data instead which will be passed through here.
     */
    fun getPokemonFavouriteStatus(pokemonName: String) {
        viewModelScope.launch {
            try {
                favouritesRepository.getFavouriteStatus(pokemonName)
            } catch (error: Throwable) {
                println(error)
                notifyError(error.message)
            }
        }
    }

    /**
     * Set the favourite status of a particular Pokemon.
     */
    fun setPokemonFavouriteStatus(pokemonName: String, isFavourite: Boolean) {
        viewModelScope.launch {
            try {
                favouritesRepository.setFavouriteStatus(pokemonName, isFavourite)
            } catch (error: Throwable) {
                println(error)
                notifyError(error.message)
            }
        }
    }

    /**
     * Get a Pokemon's evolution chain as a [PokemonEvolutionChain].
     */
    fun getPokemonEvolutionChain(pokedexNumber: Int) {
        viewModelScope.launch {
            try {
                val chain = pokeApiRepository.getPokemonEvolutionChain(pokedexNumber)
                _currentEvolutionChain.value = chain
                _currentEvolutionChain.value = null
            } catch (error: PokeDataRepository.PokeDataRepositoryException) {
                println(error.stackTrace)
                error.message?.let { notifyError(it) }
            }
        }
    }

    private fun notifyError(message: String?) {
        _error.value = message
        _error.value = null
    }
}