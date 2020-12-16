package com.example.pokedata.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedata.R
import com.example.pokedata.rest.pokedex.PokemonResource
import kotlinx.android.synthetic.main.item_pokedex_pokemon.view.*

class PokedexAdapter(private val pokemonList: List<PokemonResource>) : RecyclerView.Adapter<PokedexAdapter.ViewHolder>(){

    private lateinit var context: Context

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_pokedex_pokemon, parent, false)
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return pokemonList.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(pokemonList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun databind(pokemon: PokemonResource) {
            Glide.with(context).load(pokemon.sprites.front_default).into(itemView.ivPokemonImage)
            itemView.tvPokemonName.text = pokemon.name
            itemView.tvPokemonNumber.text = "#${pokemon.id.toString().padStart(3, '0')}"
        }
    }
}