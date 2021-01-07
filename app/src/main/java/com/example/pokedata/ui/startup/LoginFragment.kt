package com.example.pokedata.ui.startup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pokedata.R
import com.example.pokedata.vm.AuthenticationViewModel
import com.example.pokedata.vm.PokedexViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_pokedex.*

class LoginFragment : Fragment() {
    private val viewModel: AuthenticationViewModel by activityViewModels()
    private var isLoginMode: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val backButtonCallBack = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finishAndRemoveTask()
        }
        backButtonCallBack.isEnabled = true
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeAuthentication()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    private fun initViews() {
        tvHaveAccount.setOnClickListener {
            switchMode()
        }
        updateViews(this.isLoginMode)
    }

    private fun switchMode() {
        this.isLoginMode = !this.isLoginMode
        updateViews(this.isLoginMode)
    }

    private fun updateViews(isLoginMode: Boolean) {
        if (isLoginMode) {
            tfSignUpPasswordConfirm.isGone = true;
            btnSignup.text = getString(R.string.btnLogin)
            btnSignup.setOnClickListener {
                attemptLogin()
            }
            tvHaveAccount.text = getString(R.string.tvDontHaveAccount)
            tvLoginHelp.text = getString(R.string.tvLoginHelp)
        } else {
            tfSignUpPasswordConfirm.isGone = false;
            btnSignup.text = getString(R.string.btnSignup)
            btnSignup.setOnClickListener {
                attemptSignUp()
            }
            tvHaveAccount.text = getString(R.string.tvHaveAccount)
            tvLoginHelp.text = getString(R.string.tvSignUpHelp)

        }
    }

    private fun attemptSignUp() {
        viewModel.createUser(
            etSignUpEmail.text.toString(),
            etSignUpPassword.text.toString(),
            etSignUpPasswordConfirm.text.toString()
        )
    }

    private fun attemptLogin() {
        viewModel.loginUser(etSignUpEmail.text.toString(), etSignUpPassword.text.toString())
    }

    private fun observeAuthentication() {
        viewModel.authenticationError.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                notify(it)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                notify(it)
            }
        })
        viewModel.loginSuccess.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                continueAfterAuthentication()
                notify(it)
            }
        })
        viewModel.createUserSuccess.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                notify(it)
            }
        })
    }

    private fun continueAfterAuthentication() {
        findNavController().navigate(R.id.action_loginFragment_to_pokeDexFragment2)
    }

    private fun notify(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
    }

}