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
    private val perPage: Int = 40
    private val currentPokemonLoaded = mutableListOf<PokemonResource>()

    private val pokeApiRepository = PokeApiRepository()

    private val _pokemonOnPage = MutableLiveData<MutableList<PokemonResource>>()
    val pokemonOnPage: LiveData<MutableList<PokemonResource>> get() = _pokemonOnPage

    fun getPokedexNextPage() {
        viewModelScope.launch {
            try {
                if (totalPokemonCount == null) {
                    totalPokemonCount = pokeApiRepository.getPokemonLimit()
                }
                currentPokemonLoaded.addAll(pokeApiRepository.getPokemonPaginated(offset+perPage, offset))
                offset += perPage;
                _pokemonOnPage.value = currentPokemonLoaded
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
            }
        }
    }
}