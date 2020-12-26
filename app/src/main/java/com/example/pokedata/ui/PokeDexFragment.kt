package com.example.pokedata.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedata.R
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.utils.EndlessRecyclerViewScroll
import com.example.pokedata.vm.PokedexViewModel
import kotlinx.android.synthetic.main.fragment_pokedex.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PokeDexFragment : Fragment() {
    private val pokemon = arrayListOf<PokemonBasic>()
    private val pokedexAdapter = PokedexAdapter(pokemon)
    private val viewModel: PokedexViewModel by activityViewModels()

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
        viewModel.getPokedexNextPage()
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
        this.viewModel.getPokedexNextPage()
    }

    private fun observePokedexPagination() {
        viewModel.pokemonOnPage.observe(viewLifecycleOwner, { pokemon ->
            println("Received Pokemon LiveData")
            println(pokemon)
            if (pokemon != null) {
                this.pokemon.clear()
                this.pokemon.addAll(pokemon)
                pokedexAdapter.notifyDataSetChanged()
            }
        })
        viewModel.canGoNextPage.observe(viewLifecycleOwner, {
            endlessScrollListener.canCall = it
            pgPokedex.isGone = it
        })
        viewModel.endReached.observe(viewLifecycleOwner, {
            val endReached = it;
            endlessScrollListener.canCall = !endReached
            pgPokedex.isGone = endReached
        })
    }
}