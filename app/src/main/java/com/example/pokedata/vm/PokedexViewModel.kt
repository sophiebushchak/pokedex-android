package com.example.pokedata.vm

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.rest.PokeApiRepository
import kotlinx.coroutines.launch

class PokedexViewModel(application: Application) : AndroidViewModel(application) {
    private var totalPokemonInPokedex: Int = 0;
    var offset: Int = 0
    private val perPage: Int = 100
    private val currentPokemonLoaded = mutableListOf<PokemonBasic>()

    private val pokeApiRepository = PokeApiRepository(application.applicationContext)

    private val _pokemonOnPage = MutableLiveData<MutableList<PokemonBasic>>()
    val pokemonOnPage: LiveData<MutableList<PokemonBasic>> get() = _pokemonOnPage

    private val _canGoNextPage = MutableLiveData<Boolean>()
    val canGoNextPage: LiveData<Boolean> get() = _canGoNextPage

    private val _endReached = MutableLiveData<Boolean>()
    val endReached: LiveData<Boolean> get() = _endReached

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
            }
        }
    }
}
