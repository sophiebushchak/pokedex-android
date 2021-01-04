package com.example.pokedata.ui.welcomesplash

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pokedata.R
import com.example.pokedata.vm.AuthenticationViewModel

class WelcomeSplashFragment : Fragment() {
    private val viewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLoggedInStatus()
        viewModel.getLoggedInStatus()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    private fun observeLoggedInStatus() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner, {
            if (it) {
                val splashEndHandler = Handler()
                splashEndHandler.postDelayed(
                    { findNavController().navigate(R.id.action_welcomeSplashFragment_to_pokeDexFragment2) },
                    1500
                )
            } else {
                val splashEndHandler = Handler()
                splashEndHandler.postDelayed(
                    { findNavController().navigate(R.id.action_welcomeSplashFragment_to_loginFragment) },
                    1500
                )
            }
        })
        viewModel.authenticationError.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}