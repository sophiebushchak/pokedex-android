package com.example.pokedata.ui.startup

import android.os.Bundle
import android.os.Handler
import android.util.Log
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
    private val TAG = "SplashFragment"
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
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    /**
     * Navigate to either the Pokedex screen or the Login/Signup screen depending on whether the
     * user is logged in.
     */
    private fun observeLoggedInStatus() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner, {
            Log.d(TAG, "login status: $it")
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