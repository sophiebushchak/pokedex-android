package com.example.pokedata.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedata.R
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.utils.EndlessRecyclerViewScroll
import com.example.pokedata.vm.PokedexViewModel
import com.example.pokedata.vm.PokemonDetailViewModel
import kotlinx.android.synthetic.main.fragment_pokedex.*

const val POKEMON_KEY = "pokemon_key"
const val POKEMON_BUNDLE = "pokemon_bundle"

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observePokedexPagination()
        observeError()
        pokedexViewModel.getPokedexNextPage()
    }

    private fun initViews() {
        val gridlLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        rvPokedex.layoutManager = gridlLayoutManager
        rvPokedex.adapter = pokedexAdapter
        endlessScrollListener = EndlessRecyclerViewScroll({ getNextPage() }, true)
        rvPokedex.addOnScrollListener(endlessScrollListener)
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
        pokedexViewModel.canGoNextPage.observe(viewLifecycleOwner, {
            endlessScrollListener.canCall = it
            pgPokedex.isGone = it
        })
        pokedexViewModel.endReached.observe(viewLifecycleOwner, {
            val endReached = it;
            endlessScrollListener.canCall = !endReached
            pgPokedex.isGone = endReached
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