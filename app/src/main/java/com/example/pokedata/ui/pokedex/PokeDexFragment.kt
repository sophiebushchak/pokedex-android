package com.example.pokedata.ui.pokedex

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedata.R
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.utils.EndlessRecyclerViewScroll
import com.example.pokedata.vm.PokedexViewModel
import com.example.pokedata.vm.PokemonDetailViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_pokedex.*

/**
 * Fragment that displays the Pokedex with a RecyclerView.
 * It can navigate to itself to support Search and showing the Favourite Pokemon.
 */
class PokeDexFragment : Fragment() {
    private val pokemon = arrayListOf<PokemonBasic>()
    private val pokedexAdapter = PokedexAdapter(pokemon, ::onClickPokemon)

    private val pokedexViewModel: PokedexViewModel by activityViewModels()
    private val pokemonDetailViewModel: PokemonDetailViewModel by activityViewModels()

    private lateinit var endlessScrollListener: EndlessRecyclerViewScroll

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Set up custom back button logic so that the search and favourite can be popped out of while retaining
        //previous Pokemon and position in the RecyclerView.
        val backButtonCallBack = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!searchModal.isGone) {
                closeSearchMenu()
            } else if (pokedexViewModel.getPokedexStatus() == PokedexViewModel.PokedexStatus.Main) {
                requireActivity().finishAndRemoveTask()
            } else {
                pokedexViewModel.popPokemonStack()
                findNavController().popBackStack()
            }
        }
        backButtonCallBack.isEnabled = true
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_pokedex, container, false)
    }

    //Inflate menu depending on if this is the main Pokedex or an instance of this fragment that was
    //navigated to and can be popped out of. Also sets the title in the action bar to match.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val actionBar = (context as AppCompatActivity).supportActionBar
        when (pokedexViewModel.getPokedexStatus()) {
            PokedexViewModel.PokedexStatus.Search -> run {
                inflater.inflate(R.menu.menu_poppable, menu)
                actionBar?.setTitle(PokedexViewModel.PokedexStatus.Search.name)
                true
            }
            PokedexViewModel.PokedexStatus.Favourites -> run {
                inflater.inflate(R.menu.menu_poppable, menu)
                actionBar?.setTitle(PokedexViewModel.PokedexStatus.Favourites.name)
                true
            }
            else -> {
                inflater.inflate(R.menu.menu_main, menu)
                actionBar?.setTitle(getString(R.string.app_name))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            this.openSearchMenu()
            true
        }
        R.id.action_view_favourites -> {
            this.findFavourites()
            true
        }
        else -> {
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokedexPagination()
        observeError()
        initViews()
    }

    //Initialize the RecyclerView and sets the searchModal to be gone by default.
    private fun initViews() {
        searchModal.isGone = true;
        val gridlLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        rvPokedex.layoutManager = gridlLayoutManager
        rvPokedex.adapter = pokedexAdapter
        endlessScrollListener = EndlessRecyclerViewScroll({ getNextPage() }, true)
        rvPokedex.addOnScrollListener(endlessScrollListener)
    }

    //Opens the search modal
    private fun openSearchMenu() {
        searchModal.isClickable = true
        searchModal.isGone = false
        ivCloseSearch.setOnClickListener {
            this.closeSearchMenu()
        }
        btnSearch.setOnClickListener {
            this.searchPokemon()
        }
    }

    //Close the search modal
    private fun closeSearchMenu() {
        etSearchField.text?.clear()
        searchModal.isGone = true
    }

    //Search for a Pokemon by name using the ViewModel
    private fun searchPokemon() {
        pokedexViewModel.searchPokemon(etSearchField.text.toString())
    }

    //Find favourites using the ViewModel
    private fun findFavourites() {
        pokedexViewModel.findFavourites()
    }

    //Get the next page of Pokemon
    private fun getNextPage() {
        println("Loading next page!")
        this.pokedexViewModel.getPokedexNextPage()
    }

    //Navigate to a Pokemon's detail page
    private fun onClickPokemon(pokemon: PokemonBasic) {
        pokemonDetailViewModel.getPokemonDetailed(pokemon.pokedexNumber)
        findNavController().navigate(R.id.action_pokeDexFragment2_to_pokemonDetailFragment)
    }

    //Observe ViewModel LiveData
    private fun observePokedexPagination() {
        pokedexViewModel.pokemonOnPage.observe(viewLifecycleOwner, { pokemon ->
            println("Received Pokemon LiveData")
            println(pokemon)
            if (pokemon != null) {
                this.pokemon.clear()
                this.pokemon.addAll(pokemon)
                pokedexAdapter.notifyDataSetChanged()
            }
        })
        //When searching, display progress indicator
        pokedexViewModel.isGettingPage.observe(viewLifecycleOwner, {
            pgPokedex.isGone = !it
        })
        //Disable triggering the next page when a page is already loading
        pokedexViewModel.canGoNextPage.observe(viewLifecycleOwner, {
            endlessScrollListener.canCall = it
        })
        //Disable triggering the next page when end of the Pokedex is reached
        pokedexViewModel.currentEndReached.observe(viewLifecycleOwner, {
            val endReached = it;
            endlessScrollListener.canCall = !endReached
        })
        //Navigate to itself when a search is successfully done
        pokedexViewModel.searchComplete.observe(viewLifecycleOwner, {
            if (it) {
                closeSearchMenu()
                findNavController().navigate(R.id.action_pokeDexFragment2_self)
            }
        })
    }

    //Notify user about error through a Snackbar
    private fun observeError() {
        pokedexViewModel.error.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

}