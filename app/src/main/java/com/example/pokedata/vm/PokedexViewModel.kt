package com.example.pokedata.vm

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.firebase.FavouritesRepository
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.rest.PokeApiRepository
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PokedexViewModel(application: Application) : AndroidViewModel(application) {
    private val pokeApiRepository = PokeApiRepository(application.applicationContext)
    private val favouritesRepository: FavouritesRepository = FavouritesRepository()

    private val pokemonLoaded: Stack<Pair<PokedexStatus, ArrayList<PokemonBasic>>> = Stack()

    private var totalPokemonInPokedex: Int = 0;
    var offset: Int = 0
    private val perPage: Int = 100
    private var lastPageReached: Boolean = false;

    private val _pokemonOnPage = MutableLiveData<MutableList<PokemonBasic>>()
    val pokemonOnPage: LiveData<MutableList<PokemonBasic>> get() = _pokemonOnPage

    private val _searchComplete = MutableLiveData<Boolean>()
    val searchComplete: LiveData<Boolean> get() = _searchComplete

    private val _canGoNextPage = MutableLiveData<Boolean>()
    val canGoNextPage: LiveData<Boolean> get() = _canGoNextPage

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> get() = _isSearching

    private val _currentEndReached = MutableLiveData<Boolean>()
    val currentEndReached: LiveData<Boolean> get() = _currentEndReached

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        pokemonLoaded.push(Pair(PokedexStatus.Main, ArrayList()))
        getPokedexNextPage()
    }

    fun getPokedexNextPage() {
        viewModelScope.launch {
            try {
                if (!lastPageReached) {
                    _isSearching.value = true;
                    _canGoNextPage.value = false
                    if (totalPokemonInPokedex == 0) {
                        totalPokemonInPokedex = pokeApiRepository.getTotalPokemon()
                    }
                    val currentLoaded = pokemonLoaded[0].second
                    currentLoaded.addAll(pokeApiRepository.getPokemonPaginated(offset, perPage))
                    val newOffset = offset + perPage
                    offset = newOffset
                    _pokemonOnPage.value = currentLoaded
                    _canGoNextPage.value = true
                    _isSearching.value = false
                    if (offset > totalPokemonInPokedex) {
                        lastPageReached = true
                        _currentEndReached.value = lastPageReached
                    }
                }
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
                error.message?.let { notifyError(it) }
            }
        }
    }

    fun searchPokemon(pokemonName: String) {
        viewModelScope.launch {
            try {
                _canGoNextPage.value = false
                val results = pokeApiRepository.getPokemonWithSearch(pokemonName)
                if (results.isNotEmpty()) {
                    pokemonLoaded.push(Pair(PokedexStatus.Search, ArrayList()))
                    val newPokemon = pokemonLoaded.peek().second
                    newPokemon.addAll(results)
                    _pokemonOnPage.value = newPokemon
                }
                _searchComplete.value = true
                _searchComplete.value = false;
            } catch (error: PokeApiRepository.PokeApiError) {
                println(error)
                if (!lastPageReached && pokemonLoaded.size == 1) {
                    _currentEndReached.value = false
                    _canGoNextPage.value = true
                }
                error.message?.let { notifyError(it) }
            }
        }
    }

    fun findFavourites() {
        viewModelScope.launch {
            try {
                _canGoNextPage.value = false
                val favouritesList = favouritesRepository.getListOfAllFavouritesByName()
                val filteredToFavouritedOnly = favouritesList.filter { pair ->
                    pair.second
                }
                val pokemonList: ArrayList<PokemonBasic> = arrayListOf()
                filteredToFavouritedOnly.forEach { pair ->
                    pokemonList.add(pokeApiRepository.getPokemonWithSearch(pair.first)[0])
                }
                if (pokemonList.isNotEmpty()) {
                    pokemonList.sortBy { it.pokedexNumber }
                    pokemonLoaded.push(Pair(PokedexStatus.Favourites, pokemonList))
                    _pokemonOnPage.value = pokemonList
                }
                _searchComplete.value = true
                _searchComplete.value = false
            } catch (error: Throwable) {
                println(error)
                error.message?.let {notifyError(it)}
            }
        }
    }

    fun popPokemonStack() {
        viewModelScope.launch {
            try {
                if (pokemonLoaded.size > 1) {
                    pokemonLoaded.pop()
                    _pokemonOnPage.value = pokemonLoaded.peek().second
                    if (!lastPageReached && pokemonLoaded.size == 1) {
                        _canGoNextPage.value = true
                    }
                }
            } catch (error: Throwable) {
                println(error)
            }
        }
    }

    fun getPokedexStatus(): PokedexStatus {
        return pokemonLoaded.peek().first
    }

    private fun notifyError(message: String) {
        _error.value = message
        _error.value = ""
    }

    enum class PokedexStatus() {
        Main,
        Search,
        Favourites
    }
}
