package com.example.pokedata.ui.pokemondetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedata.App
import com.example.pokedata.R
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.PokeDataApiConfig
import com.example.pokedata.rest.response.PokemonEvolutionChain
import kotlinx.android.synthetic.main.item_pokemon_detail_evolutions.view.*
import kotlinx.android.synthetic.main.item_pokemon_detail_information.view.*

class PokemonDetailAdapter(
    var pokemon: PokemonDetailed?,
    var evolutionChain: PokemonEvolutionChain?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val resources = App.getRes()
    private val TAB_COUNT = 2;

    //LiveData for clicking on a Pokemon evolution
    private val _pokemonEvolutionSelected = MutableLiveData<Int>()
    val pokemonEvolutionSelected: LiveData<Int> get() = _pokemonEvolutionSelected

    //Inflate ViewHolder depending on position
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> PokemonInfoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pokemon_detail_information, parent, false)
            )
            1 -> PokemonEvolutionsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pokemon_detail_evolutions, parent, false)
            )
            else -> throw Error("Invalid position")
        }
    }

    //Set the ViewHolder to be either of two ViewHolders and databind it
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val viewHolder = holder as PokemonInfoViewHolder
                this.pokemon?.let { viewHolder.databind(it) }
            }
            1 -> {
                val viewHolder = holder as PokemonEvolutionsViewHolder
                this.evolutionChain?.let { viewHolder.databind(it) }
            }
        }
    }

    //There are always 2 tabs
    override fun getItemCount(): Int {
        return TAB_COUNT;
    }

    //The View is dependent on what tab the user is on.
    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class PokemonInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun databind(pokemon: PokemonDetailed) {
            itemView.tvHeightValue.text =
                resources.getString(R.string.tvHeightValue, pokemon.height.toDouble() / 10)
            itemView.tvWeightValue.text =
                resources.getString(R.string.tvWeightValue, pokemon.weight.toDouble() / 10)
            itemView.tvPokedexEntry.text = pokemon.pokedexEntryDescription.replace("\n", " ")
        }
    }

    inner class PokemonEvolutionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun databind(chain: PokemonEvolutionChain) {
            chain.first?.let {
                val pokemon = it
                itemView.tvEvolution1.text = pokemon.pokemonName
                Glide.with(itemView.context).load(PokeDataApiConfig.HOST + pokemon.sprites.front)
                    .into(itemView.ivEvolution1)
                itemView.ivEvolution1.setOnClickListener {
                    _pokemonEvolutionSelected.value = pokemon.pokedexNumber
                }
            }
            chain.second?.let {
                val pokemon = it
                itemView.tvEvolution2.isGone = false;
                itemView.tvEvolution2.text = it.pokemonName
                itemView.ivArrowFirstEvolution.isGone = false;
                itemView.ivEvolution2.isGone = false;
                Glide.with(itemView.context).load(PokeDataApiConfig.HOST + it.sprites.front)
                    .into(itemView.ivEvolution2)
                itemView.ivEvolution2.setOnClickListener {
                    _pokemonEvolutionSelected.value = pokemon.pokedexNumber
                }
            } ?: run {
                itemView.tvEvolution2.isGone = true;
                itemView.ivArrowFirstEvolution.isGone = true
                itemView.ivEvolution2.isGone = true
            }
            chain.third?.let {
                val pokemon = it
                itemView.tvEvolution3.isGone = false;
                itemView.tvEvolution3.text = it.pokemonName
                itemView.ivArrowSecondEvolution.isGone = false;
                itemView.ivEvolution3.isGone = false;
                Glide.with(itemView.context).load(PokeDataApiConfig.HOST + it.sprites.front)
                    .into(itemView.ivEvolution3)
                itemView.ivEvolution3.setOnClickListener {
                    _pokemonEvolutionSelected.value = pokemon.pokedexNumber
                }
            } ?: run {
                itemView.tvEvolution3.isGone = true;
                itemView.ivArrowSecondEvolution.isGone = true
                itemView.ivEvolution3.isGone = true
            }
        }
    }

}