package com.example.pokedata.vm

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.viewModelScope
import com.example.pokedata.rest.PokeApiRepository
import kotlinx.coroutines.launch

class PokeDexRetrieveViewModel(application: Application) : AndroidViewModel(application) {
    private val pokeApiRepository = PokeApiRepository()

    val progressCount = pokeApiRepository.progressCount

    fun getPokedexFromAPI() {
        viewModelScope.launch {
            try {
                val pokemon = pokeApiRepository.getAllPokemonForNationalDex()
                println(pokemon)
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
            }
        }
    }
}