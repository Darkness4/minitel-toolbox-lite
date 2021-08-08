package com.minitel.toolboxlite.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.minitel.toolboxlite.core.state.fold
import com.minitel.toolboxlite.databinding.FragmentLoginBinding
import com.minitel.toolboxlite.presentation.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private var loginJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        loginJob = lifecycleScope.launch {
            viewModel.login.collect {
                it?.fold(
                    {
                        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                    },
                    { e ->
                        Toast.makeText(
                            context,
                            e.localizedMessage ?: "Something wrong happened",
                            Toast.LENGTH_LONG
                        ).show()
                        Timber.e(e)
                    }
                )
                it?.let { viewModel.loginFinished() }
            }
        }
    }

    override fun onStop() {
        loginJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
