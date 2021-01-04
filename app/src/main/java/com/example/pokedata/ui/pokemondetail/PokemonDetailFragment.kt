package com.example.pokedata.ui.pokemondetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.pokedata.R
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.PokeApiConfig
import com.example.pokedata.vm.PokemonDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_pokemon_detail.*
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PokemonDetailFragment : Fragment() {
    private val pokemonDetailAdapter = PokemonDetailAdapter(null, null)
    private val viewModel: PokemonDetailViewModel by activityViewModels()
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokemon_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemon()
        observeEvolutionChain()
        observeFavouriteStatus()
        val viewPager: ViewPager2 = pokemonDetailPager
        viewPager.adapter = pokemonDetailAdapter
        tabLayout = pokemonDetailTabLayout
        //Always 2 same tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Info"
            } else {
                tab.text = "Evolution"
            }
        }.attach()
    }

    private fun updatePokemonInformation(pokemon: PokemonDetailed) {
        val context = requireContext()
        Glide.with(context).load(PokeApiConfig.HOST + pokemon.sprites.front).into(ivPokemonDetail)
        tvPokemonDetailName.text = pokemon.pokemonName
        tvPokemonDetailGenus.text = pokemon.genus
        tvPokemonDetailPokedexNumber.text = "#${pokemon.pokedexNumber.toString().padStart(3, '0')}"
        tvPokemonDetailPrimaryType.text = pokemon.primaryType.capitalize(Locale.ENGLISH)
        val primaryType = PokemonBasic.PokemonType.valueOf(pokemon.primaryType)
        tvPokemonDetailPrimaryType.setBackgroundColor(
            resources.getColor(
                primaryType.typeColor,
                context.theme
            )
        )
        toplayout.setBackgroundColor(
            context.resources.getColor(
                primaryType.typeBackground,
                context.theme
            )
        )
        if (!pokemon.secondaryType.isNullOrBlank()) {
            tvPokemonDetailSecondaryType.isGone = false
            val secondaryType = PokemonBasic.PokemonType.valueOf(pokemon.secondaryType)
            tvPokemonDetailSecondaryType.setBackgroundColor(
                context.resources.getColor(
                    secondaryType.typeColor,
                    context.theme
                )
            )
            tvPokemonDetailSecondaryType.text = pokemon.secondaryType.capitalize(Locale.ENGLISH)
        } else {
            tvPokemonDetailSecondaryType.isGone = true
        }
    }

    private fun updateFavouriteButton(pokemonName: String, isFavourite: Boolean) {
        if (isFavourite) {
            ivFavouriteButton.setImageResource(R.drawable.ic_heart)
        } else {
            ivFavouriteButton.setImageResource(R.drawable.ic_heart_outline)
        }
        ivFavouriteButton.setOnClickListener {
            viewModel.setPokemonFavouriteStatus(pokemonName, !isFavourite)
        }
    }

    private fun observePokemon() {
        viewModel.currentPokemon.observe(viewLifecycleOwner, {
            if (it != null) {
                pokemonDetailAdapter.pokemon = it
                updatePokemonInformation(it)
                pokemonDetailAdapter.notifyDataSetChanged()
                viewModel.getPokemonEvolutionChain(it.pokedexNumber)
                viewModel.getPokemonFavouriteStatus(it.pokemonName)
            }
        })
    }

    private fun observeEvolutionChain() {
        viewModel.currentEvolutionChain.observe(viewLifecycleOwner, {
            if (it != null) {
                println(it)
                pokemonDetailAdapter.evolutionChain = it
                pokemonDetailAdapter.notifyDataSetChanged()
            }
        })
        pokemonDetailAdapter.pokemonEvolutionSelected.observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.getPokemonDetailed(it)
                tabLayout.selectTab(tabLayout.getTabAt(0))
            }
        })
    }

    private fun observeFavouriteStatus() {
        viewModel.favouriteStatus.observe(viewLifecycleOwner, { favouriteStatus ->
            if (favouriteStatus != null) {
                updateFavouriteButton(favouriteStatus.first, favouriteStatus.second)
            }
        })
        viewModel.favourited.observe(viewLifecycleOwner, {
            if (it != null) {
                var message = "Added ${it.first} to favourites"
                if (!it.second) {
                    message = "Removed ${it.first} from favourites"
                }
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                updateFavouriteButton(it.first, it.second)
            }
        })
    }
}
