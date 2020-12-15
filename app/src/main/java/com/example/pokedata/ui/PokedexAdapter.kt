package com.example.pokedata.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedata.R
import com.example.pokedata.models.PokemonTest
import kotlinx.android.synthetic.main.item_pokedex_pokemon.view.*

import java.text.SimpleDateFormat

class PokedexAdapter(private val backlog: List<PokemonTest>) : RecyclerView.Adapter<PokedexAdapter.ViewHolder>(){

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_pokedex_pokemon, parent, false)
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return backlog.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(backlog[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun databind(pokemon: PokemonTest) {
            itemView.tvPokemonName.text = pokemon.name
            itemView.tvPokemonNumber.text = "#00${pokemon.pokedexNumber}"
        }
    }
}