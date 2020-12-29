package com.example.pokedata.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.PokeApiRepository
import com.example.pokedata.rest.response.PokemonEvolutionChain
import com.example.pokedata.ui.PokemonDetailAdapter
import kotlinx.coroutines.launch

class PokemonDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val pokeApiRepository = PokeApiRepository(application.applicationContext)

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _currentPokemon = MutableLiveData<PokemonDetailed>()
    val currentPokemon: LiveData<PokemonDetailed> get() = _currentPokemon

    private val _currentEvolutionChain = MutableLiveData<PokemonEvolutionChain>()
    val currentEvolutionChain: LiveData<PokemonEvolutionChain> get() = _currentEvolutionChain

    fun getPokemonDetailed(pokedexNumber: Int) {
        viewModelScope.launch {
            try {
                val pokemon = pokeApiRepository.getPokemonByPokedexNumber(pokedexNumber)
                println(pokemon)
                _currentPokemon.value = pokemon
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
                error.message?.let { notifyError(it) }
            }
        }
    }

    fun getPokemonEvolutionChain(pokedexNumber: Int) {
        viewModelScope.launch {
            try {
                val chain = pokeApiRepository.getPokemonEvolutionChain(pokedexNumber)
                println(chain)

            } catch (error: PokeApiRepository.PokeApiError) {
                println(error.stackTrace)
                error.message?.let { notifyError(it) }
            }
        }
    }

    private fun notifyError(message: String) {
        _error.value = message
    }
}