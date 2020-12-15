package com.example.pokedata.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedata.R
import com.example.pokedata.models.PokemonTest
import com.example.pokedata.vm.PokeDexRetrieveViewModel
import kotlinx.android.synthetic.main.fragment_pokedex.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PokeDexFragment : Fragment() {
    private val pokemon = arrayListOf<PokemonTest>()
    private val pokedexAdapter = PokedexAdapter(pokemon)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pokemon.add(PokemonTest("Bulbasaur", 1, listOf("Grass"), R.drawable.ic_launcher_background))
        initViews()
    }

    private fun initViews() {
        val gridlLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        rvPokedex.layoutManager = gridlLayoutManager
        rvPokedex.adapter = pokedexAdapter
    }
}