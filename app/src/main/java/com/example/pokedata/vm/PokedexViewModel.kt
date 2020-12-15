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
    var offset: Int = 1
    private val perPage: Int = 50
    private val pokeApiRepository = PokeApiRepository()

    val progressCount = pokeApiRepository.progressCount

    private val _pokemonOnPage = MutableLiveData<MutableList<PokemonResource>>()
    val pokemonOnPage: LiveData<MutableList<PokemonResource>> get() = _pokemonOnPage

    fun getPokedexNextPage() {
        viewModelScope.launch {
            try {
                if (totalPokemonCount == null) {
                    totalPokemonCount = pokeApiRepository.getPokemonLimit()
                }
                val pokemon = mutableListOf<PokemonResource>()
                pokemon.addAll(pokeApiRepository.getPokemonPaginated(offset+perPage, offset))
                offset += perPage;
                println(pokemon)
                _pokemonOnPage.value = pokemon

            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
            }
        }
    }
}