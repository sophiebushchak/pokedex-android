package com.example.pokedata.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.pokedata.R
import com.example.pokedata.vm.PokeDexRetrieveViewModel
import kotlinx.android.synthetic.main.fragment_poke_dex_retrieve.*

class PokeDexRetrieveFragment : Fragment() {
    private val viewModel: PokeDexRetrieveViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_poke_dex_retrieve, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProgress()
        viewModel.getPokedexFromAPI()
    }

    private fun observeProgress() {
        viewModel.progressCount.observe(viewLifecycleOwner, Observer {
            count ->
            if (count != null) {
                tvProgress.text = count.toString()
            }
        })
    }
}