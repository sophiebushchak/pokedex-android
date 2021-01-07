package com.example.pokedata.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.firebase.FavouritesRepository
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.PokeApiRepository
import com.example.pokedata.rest.response.PokemonEvolutionChain
import kotlinx.coroutines.launch

class PokemonDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val pokeApiRepository = PokeApiRepository(application.applicationContext)
    private val favouritesRepository = FavouritesRepository()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    val favouriteStatus: LiveData<Pair<String, Boolean>?> get() = favouritesRepository.favouriteStatus
    val favourited: LiveData<Pair<String, Boolean>?> get() = favouritesRepository.favourited

    private val _currentPokemon = MutableLiveData<PokemonDetailed?>()
    val currentPokemon: LiveData<PokemonDetailed?> get() = _currentPokemon

    private val _currentEvolutionChain = MutableLiveData<PokemonEvolutionChain?>()
    val currentEvolutionChain: LiveData<PokemonEvolutionChain?> get() = _currentEvolutionChain

    fun getPokemonDetailed(pokedexNumber: Int) {
        viewModelScope.launch {
            try {
                val pokemon = pokeApiRepository.getPokemonByPokedexNumber(pokedexNumber)
                println(pokemon)
                _currentPokemon.value = pokemon
                _currentPokemon.value = null
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
                notifyError(error.message)
            }
        }
    }

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

    fun setPokemonFavouriteStatus(pokemonName: String, isFavourite: Boolean) {
        viewModelScope.launch {
            try {
                favouritesRepository.setFavouriteStatus(pokemonName, isFavourite)
            } catch(error: Throwable) {
                println(error)
                notifyError(error.message)
            }
        }
    }

    fun getPokemonEvolutionChain(pokedexNumber: Int) {
        viewModelScope.launch {
            try {
                val chain = pokeApiRepository.getPokemonEvolutionChain(pokedexNumber)
                _currentEvolutionChain.value = chain
                _currentEvolutionChain.value = null
            } catch (error: PokeApiRepository.PokeApiError) {
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