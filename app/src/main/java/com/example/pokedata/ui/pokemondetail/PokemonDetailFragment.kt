package com.example.pokedata.ui.pokemondetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.pokedata.R
import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.models.PokemonDetailed
import com.example.pokedata.rest.PokeDataApiConfig
import com.example.pokedata.vm.PokemonDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_pokemon_detail.*
import java.util.*

/**
 * Fragment for displaying detailed information about a Pokemon
 */
class PokemonDetailFragment : Fragment() {
    private val TAG = "PokemonDetailFragment"

    private val pokemonDetailAdapter = PokemonDetailAdapter(null, null)
    private val viewModel: PokemonDetailViewModel by activityViewModels()
    private lateinit var tabLayout: TabLayout
    private var showShiny = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokemon_detail, container, false)
    }

    //Observe LiveData and set up ViewPager2 with adapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePokemon()
        observeEvolutionChain()
        observeFavouriteStatus()
        observeError()
        val viewPager: ViewPager2 = pokemonDetailPager
        viewPager.adapter = pokemonDetailAdapter
        tabLayout = pokemonDetailTabLayout
        //Always 2 same tabs
        val FIRST_TAB_POSITION = 0;
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == FIRST_TAB_POSITION) {
                tab.text = "Info"
            } else {
                tab.text = "Evolution"
            }
        }.attach()
    }

    //Update the information shown on the upper part of the Fragment
    private fun updatePokemonInformation(pokemon: PokemonDetailed) {
        this.showShiny = false
        val context = requireContext()
        switchSprite(pokemon)
        this.ivPokemonDetail.setOnClickListener {
            this.showShiny = !this.showShiny
            switchSprite(pokemon)
        }
        tvPokemonDetailName.text = pokemon.pokemonName
        tvPokemonDetailGenus.text = pokemon.genus
        tvPokemonDetailPokedexNumber.text = getString(R.string.pokedexNumber, pokemon.pokedexNumber.toString().padStart(3, '0'))
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
        val actionBar = (context as AppCompatActivity).supportActionBar
        actionBar?.title = pokemon.pokemonName
    }

    //Update resource of the favourite button and sets a new click listener on it that will make the
    //next click set the favourite status to the opposite of what it was on.
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

    //Switch from normal to shiny sprite and back.
    private fun switchSprite(pokemon: PokemonDetailed) {
        if (!this.showShiny) {
            Glide.with(requireContext()).load(PokeDataApiConfig.HOST + pokemon.sprites.front).into(ivPokemonDetail)
        } else {
            Glide.with(requireContext()).load(PokeDataApiConfig.HOST + pokemon.sprites.frontShiny).into(ivPokemonDetail)
        }
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner, {
            if (it != null) {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun observePokemon() {
        viewModel.currentPokemon.observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d(TAG, "Received selected Pokemon: $it")
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
                Log.d(TAG, "Received Pokemon evolution chain: $it")
                pokemonDetailAdapter.evolutionChain = it
                pokemonDetailAdapter.notifyDataSetChanged()
            }
        })
        pokemonDetailAdapter.pokemonEvolutionSelected.observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d(TAG, "Clicked on evolution in evolution chain with Pokedex number: $it")
                viewModel.getPokemonDetailed(it)
                //Go back to information tab
                tabLayout.selectTab(tabLayout.getTabAt(0))
            }
        })
    }

    private fun observeFavouriteStatus() {
        viewModel.favouriteStatus.observe(viewLifecycleOwner, { favouriteStatus ->
            if (favouriteStatus != null) {
                Log.d(TAG, "Received favourite status of selected Pokemon: $favouriteStatus")
                updateFavouriteButton(favouriteStatus.first, favouriteStatus.second)
            }
        })
        viewModel.favourited.observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d(TAG, "Received favourited livedata: $it")
                var message = getString(R.string.favouritedMessage, getString(R.string.favouriteAdded), it.first, getString(R.string.favouriteTo))
                if (!it.second) {
                    message = getString(R.string.favouritedMessage, getString(R.string.favouriteRemoved), it.first, getString(R.string.favouriteFrom))
                }
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                updateFavouriteButton(it.first, it.second)
            }
        })
    }
}
