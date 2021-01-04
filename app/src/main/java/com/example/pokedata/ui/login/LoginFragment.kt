package com.example.pokedata.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pokedata.R
import com.example.pokedata.vm.AuthenticationViewModel
import kotlinx.android.synthetic.main.fragment_login.*

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
        viewModel.createUser(etSignUpEmail.text.toString(), etSignUpPassword.text.toString(), etSignUpPasswordConfirm.text.toString())
    }

    private fun attemptLogin() {
        viewModel.loginUser(etSignUpEmail.text.toString(), etSignUpPassword.text.toString())
    }

    private fun observeAuthentication() {
        viewModel.authenticationError.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                indicateError(it)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                indicateError(it)
            }
        })
        viewModel.loginSuccess.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                continueFromAuthentication()
            }
        })
    }

    private fun continueFromAuthentication() {
        findNavController().navigate(R.id.action_loginFragment_to_pokeDexFragment2)
    }

    private fun indicateError(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}