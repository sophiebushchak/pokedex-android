package com.example.pokedata.vm

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.rest.PokeApiRepository
import com.example.pokedata.rest.pokedex.PokemonResource
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class PokedexViewModel(application: Application) : AndroidViewModel(application) {
    var totalPokemonCount: Int? = null
    var offset: Int = 0
    private val perPage: Int = 100
    private val currentPokemonLoaded = mutableListOf<PokemonResource>()

    private val pokeApiRepository = PokeApiRepository(application.applicationContext)

    private val _pokemonOnPage = MutableLiveData<MutableList<PokemonResource>>()
    val pokemonOnPage: LiveData<MutableList<PokemonResource>> get() = _pokemonOnPage

    private val _canGoNextPage = MutableLiveData<Boolean>()
    val canGoNextPage: LiveData<Boolean> get() = _canGoNextPage

    fun getPokedexNextPage() {
        viewModelScope.launch {
            try {
                if (totalPokemonCount == null) {
                    totalPokemonCount = pokeApiRepository.getPokemonLimit()
                }
                var newOffset = offset + perPage
                totalPokemonCount?.let {
                    if (offset + perPage >= it) {
                        newOffset = it
                        _canGoNextPage.value = false;
                    }
                }
                currentPokemonLoaded.addAll(pokeApiRepository.getPokemonPaginated(newOffset, offset))
                offset += perPage;
                _pokemonOnPage.value = currentPokemonLoaded
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
            }
        }
    }
}