package com.example.pokedata.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedata.R
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.utils.EndlessRecyclerViewScroll
import com.example.pokedata.vm.PokedexViewModel
import com.example.pokedata.vm.PokemonDetailViewModel
import kotlinx.android.synthetic.main.fragment_pokedex.*

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
        val backButtonCallBack = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (pokedexViewModel.isOnMainPokedex()) {
                requireActivity().finishAndRemoveTask()
            } else {
                println("Popping out of search.")
                pokedexViewModel.popPokemonStack()
                findNavController().popBackStack()
            }
        }
        backButtonCallBack.isEnabled = true
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_pokedex, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            this.openSearchMenu()
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

    private fun initViews() {
        searchModal.isGone = true;
        val gridlLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        rvPokedex.layoutManager = gridlLayoutManager
        rvPokedex.adapter = pokedexAdapter
        endlessScrollListener = EndlessRecyclerViewScroll({ getNextPage() }, true)
        rvPokedex.addOnScrollListener(endlessScrollListener)
    }

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

    private fun searchPokemon() {
        pokedexViewModel.searchPokemon(etSearchField.text.toString())
    }

    private fun closeSearchMenu() {
        etSearchField.text?.clear()
        searchModal.isGone = true
    }

    private fun getNextPage() {
        println("Loading next page!")
        this.pokedexViewModel.getPokedexNextPage()
    }

    private fun onClickPokemon(pokemon: PokemonBasic) {
        pokemonDetailViewModel.getPokemonDetailed(pokemon.pokedexNumber)
        findNavController().navigate(R.id.action_pokeDexFragment2_to_pokemonDetailFragment)
    }


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
        pokedexViewModel.isSearching.observe(viewLifecycleOwner, {
            pgPokedex.isGone = !it
        })
        pokedexViewModel.canGoNextPage.observe(viewLifecycleOwner, {
            endlessScrollListener.canCall = it
        })
        pokedexViewModel.currentEndReached.observe(viewLifecycleOwner, {
            val endReached = it;
            endlessScrollListener.canCall = !endReached
        })
        pokedexViewModel.searchComplete.observe(viewLifecycleOwner, {
            if (it) {
                closeSearchMenu()
                findNavController().navigate(R.id.action_pokeDexFragment2_self)
            }
        })
    }

    private fun observeError() {
        pokedexViewModel.error.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

}