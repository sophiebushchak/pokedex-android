package com.example.pokedata.vm

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.App
import com.example.pokedata.R
import com.example.pokedata.firebase.FavouritesRepository
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.rest.PokeDataRepository
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * ViewModel for the Pokedex with related functions for getting Pokemon by page, name and favourite status.
 */
class PokedexViewModel(application: Application) : AndroidViewModel(application) {
    private val pokeApiRepository = PokeDataRepository(application.applicationContext)
    private val favouritesRepository: FavouritesRepository = FavouritesRepository()
    private val resources = App.getRes()

    /**
     * Pokemon loaded are tracked by a stack of pairs with the first value in the pair being an indicator
     * of where this entry in the stack came from. The second value in the pair is an ArrayList of Pokemon.
     * This is used to keep track of the Pokemon that were loaded in and so that the [PokeDexFragment]
     * can pop out of its self-navigation and retain the previous loaded Pokemon.
     */
    private val pokemonLoaded: Stack<Pair<PokedexStatus, ArrayList<PokemonBasic>>> = Stack()

    private var totalPokemonInPokedex: Int = 0;
    var offset: Int = 0
    private val perPage: Int = 100
    private var lastPageReached: Boolean = false;

    private val _pokemonOnPage = MutableLiveData<MutableList<PokemonBasic>>()
    val pokemonOnPage: LiveData<MutableList<PokemonBasic>> get() = _pokemonOnPage

    //See PokedexFragment for usage of the LiveData
    private val _searchComplete = MutableLiveData<Boolean>()
    val searchComplete: LiveData<Boolean> get() = _searchComplete

    private val _canGoNextPage = MutableLiveData<Boolean>()
    val canGoNextPage: LiveData<Boolean> get() = _canGoNextPage

    private val _isGettingPage = MutableLiveData<Boolean>()
    val isGettingPage: LiveData<Boolean> get() = _isGettingPage

    private val _currentEndReached = MutableLiveData<Boolean>()
    val currentEndReached: LiveData<Boolean> get() = _currentEndReached

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        //Initialize pokemonLoaded stack by adding first empty ArrayList
        pokemonLoaded.push(Pair(PokedexStatus.Main, ArrayList()))
        //Get first page automatically
        getPokedexNextPage()
    }

    /**
     * Attempts to get the next page in the Pokedex
     */
    fun getPokedexNextPage() {
        viewModelScope.launch {
            try {
                if (!lastPageReached) {
                    _isGettingPage.value = true; //Indicate that retrieving next page
                    _canGoNextPage.value = false //Disable going to next page for now
                    if (totalPokemonInPokedex == 0) {
                        totalPokemonInPokedex = pokeApiRepository.getTotalPokemon() //Get total Pokemon for the first time if currently 0
                    }
                    val currentLoaded = pokemonLoaded[0].second //Get ArrayList of main Pokedex
                    currentLoaded.addAll(pokeApiRepository.getPokemonPaginated(offset, perPage))
                    val newOffset = offset + perPage //Set offset for pagination
                    offset = newOffset
                    _pokemonOnPage.value = currentLoaded //Notify of Pokemon loaded
                    _canGoNextPage.value = true //Enable going to the next page again
                    _isGettingPage.value = false //Indicate that no longer retrieving next page
                    if (offset > totalPokemonInPokedex) {
                        lastPageReached = true //If the offset exceeds the total Pokemon, the last page was reached.
                        _currentEndReached.value = lastPageReached
                    }
                } else {
                    throw Throwable(resources.getString(R.string.lastPageReachedError))
                }
            } catch (error: Throwable) {
                println(error)
                if (!lastPageReached) {
                    _canGoNextPage.value = true //Allow attempting to get next page again when an error occurred
                }
                error.message?.let { notifyError(it) }
            }
        }
    }

    /**
     * Search for a Pokemon by a name search term which does not have to match the name exactly.
     */
    fun searchPokemon(pokemonName: String) {
        viewModelScope.launch {
            try {
                _canGoNextPage.value = false //Do not load any pages while in a search result
                val results = pokeApiRepository.getPokemonWithSearch(pokemonName)
                if (results.isNotEmpty()) {
                    pokemonLoaded.push(Pair(PokedexStatus.Search, ArrayList()))
                    val newPokemon = pokemonLoaded.peek().second
                    newPokemon.addAll(results)
                    _pokemonOnPage.value = newPokemon
                }
                _searchComplete.value = true
                _searchComplete.value = false //Clear LiveData value so that it doesn't get used again
            } catch (error: Throwable) {
                println(error)
                if (!lastPageReached && pokemonLoaded.size == 1) {
                    _currentEndReached.value = false
                    _canGoNextPage.value = true
                }
                error.message?.let { notifyError(it) }
            }
        }
    }

    /**
     * Get favourites from Firebase
     */
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
                } else {
                    _error.value = resources.getString(R.string.noFavourites)
                }
                _searchComplete.value = true
                _searchComplete.value = false
            } catch (error: Throwable) {
                println(error)
                error.message?.let { notifyError(it) }
            }
        }
    }

    /**
     * Navigate backwards in the stack and show previous Pokemon
     */
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

    /**
     * Return the PokedexStatus of the current pokemonLoaded Pair.
     */
    fun getPokedexStatus(): PokedexStatus {
        return pokemonLoaded.peek().first
    }

    private fun notifyError(message: String?) {
        _error.value = message
        _error.value = ""
    }

    enum class PokedexStatus() {
        Main,
        Search,
        Favourites
    }
}
