package com.example.pokedata.vm

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.PokeApiRepository
import kotlinx.coroutines.launch
import okhttp3.internal.notify

class PokedexViewModel(application: Application) : AndroidViewModel(application) {
    private val pokeApiRepository = PokeApiRepository(application.applicationContext)

    private var totalPokemonInPokedex: Int = 0;
    var offset: Int = 0
    private val perPage: Int = 100
    private val currentPokemonLoaded = mutableListOf<PokemonBasic>()

    private val _pokemonOnPage = MutableLiveData<MutableList<PokemonBasic>>()
    val pokemonOnPage: LiveData<MutableList<PokemonBasic>> get() = _pokemonOnPage

    private val _canGoNextPage = MutableLiveData<Boolean>()
    val canGoNextPage: LiveData<Boolean> get() = _canGoNextPage

    private val _endReached = MutableLiveData<Boolean>()
    val endReached: LiveData<Boolean> get() = _endReached

    private val _pokemon = MutableLiveData<PokemonDetailed>()
    val pokemon: LiveData<PokemonDetailed> get() = _pokemon

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getPokedexNextPage() {
        viewModelScope.launch {
            try {
                _canGoNextPage.value = false;
                if (totalPokemonInPokedex == 0) {
                    totalPokemonInPokedex = pokeApiRepository.getTotalPokemon()
                }
                currentPokemonLoaded.addAll(pokeApiRepository.getPokemonPaginated(offset, perPage))
                val newOffset = offset + perPage
                offset = newOffset
                _pokemonOnPage.value = currentPokemonLoaded
                _canGoNextPage.value = true
                if (offset > totalPokemonInPokedex) {
                    _endReached.value = true
                }
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
                error.message?.let { notifyError(it) }
            }
        }
    }

    private fun notifyError(message: String) {
        _error.value = message
    }
}
