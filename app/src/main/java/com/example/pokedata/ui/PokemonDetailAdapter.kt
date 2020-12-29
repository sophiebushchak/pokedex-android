package com.example.pokedata.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedata.R
import com.example.pokedata.models.PokemonDetailed
import kotlinx.android.synthetic.main.item_pokemon_detail_information.view.*

class PokemonDetailAdapter(var pokemon: PokemonDetailed?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> PokemonInfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_detail_information, parent, false))
            1 -> PokemonEvolutionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_detail_evolutions, parent, false))
            else -> throw Error("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val viewHolder = holder as PokemonInfoViewHolder
                this.pokemon?.let { viewHolder.databind(it) }
            }
            1 -> {
                val viewHolder = holder as PokemonEvolutionsViewHolder
                this.pokemon?.let { viewHolder.databind(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return 2;
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class PokemonInfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun databind(pokemon: PokemonDetailed) {
            itemView.tvHeightValue.text = "${(pokemon.height.toDouble() / 10)} m"
            itemView.tvWeightValue.text = "${pokemon.weight.toDouble() / 10} kg"
            itemView.tvPokedexEntry.text = pokemon.pokedexEntryDescription.replace("\n","")
        }
    }

    inner class PokemonEvolutionsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun databind(chain: PokemonDetailed) {

        }
    }

}