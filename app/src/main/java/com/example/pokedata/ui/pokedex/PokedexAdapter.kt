package com.example.pokedata.ui.pokedex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedata.R
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.rest.PokeDataApiConfig
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_pokedex_pokemon.view.*
import java.util.*

class PokedexAdapter(
    private val pokemon: List<PokemonBasic>,
    private val onClickPokemon: (PokemonBasic) -> Unit
) : RecyclerView.Adapter<PokedexAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var lastPosition = -1

    /**
     * Creates and returns a ViewHolder object
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pokedex_pokemon, parent, false) as MaterialCardView
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int = pokemon.size

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    /**
     * Add fade in animation on items that appear when scrolling down the list
     */
    private fun setLoadInAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            view.startAnimation(animation)
            lastPosition = position
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pokemon[position])
        setLoadInAnimation(holder.itemView, position)
    }

    inner class ViewHolder(itemView: MaterialCardView) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onClickPokemon(pokemon[adapterPosition])
            }
        }

        fun bind(pokemon: PokemonBasic) {
            Glide.with(context).load(PokeDataApiConfig.HOST + pokemon.sprites.front)
                .into(itemView.ivPokemonImage)
            itemView.tvPokemonName.text = pokemon.pokemonName
            itemView.tvPokemonNumber.text = "#${pokemon.pokedexNumber.toString().padStart(3, '0')}"
            itemView.tvPokemonType1.text = pokemon.primaryType.capitalize(Locale.ENGLISH)
            val primaryType = PokemonBasic.PokemonType.valueOf(pokemon.primaryType)
            itemView.tvPokemonType1.setBackgroundColor(
                context.resources.getColor(
                    primaryType.typeColor,
                    context.theme
                )
            )
            val card = itemView as MaterialCardView
            card.setCardBackgroundColor(
                context.resources.getColor(
                    primaryType.typeBackground,
                    context.theme
                )
            )
            //Set Pokemon's secondary type in the UI to be gone when the Pokemon does not have a secondary type
            if (!pokemon.secondaryType.isNullOrBlank()) {
                itemView.tvPokemonType2.isGone = false
                val secondaryType = PokemonBasic.PokemonType.valueOf(pokemon.secondaryType!!)
                itemView.tvPokemonType2.setBackgroundColor(
                    context.resources.getColor(
                        secondaryType.typeColor,
                        context.theme
                    )
                )
                itemView.tvPokemonType2.text = pokemon.secondaryType!!.capitalize(Locale.ENGLISH)
            } else {
                itemView.tvPokemonType2.isGone = true
            }
        }

        //Remove animation when item leaves the window, this is done to prevent a bad animation bug
        fun clearAnimation() {
            itemView.clearAnimation()
        }
    }
}