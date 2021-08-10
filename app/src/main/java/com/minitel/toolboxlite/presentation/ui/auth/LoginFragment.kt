package com.minitel.toolboxlite.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.minitel.toolboxlite.core.state.doOnFailure
import com.minitel.toolboxlite.core.state.fold
import com.minitel.toolboxlite.data.datastore.loginSettingsDataStore
import com.minitel.toolboxlite.data.datastore.update
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
    private var downloadJob: Job? = null
    private var loginSettingsJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        loginSettingsJob = lifecycleScope.launch {
            requireContext().loginSettingsDataStore.data.collect {
                viewModel.rememberMe.value = it.rememberMe
                if (it.rememberMe) {
                    viewModel.username.value = it.credentials.username
                    viewModel.password.value = it.credentials.password
                }
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        loginJob = lifecycleScope.launch {
            viewModel.login.collect {
                it?.fold(
                    onSuccess = {
                        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                        requireContext().loginSettingsDataStore.update(
                            viewModel.rememberMe.value,
                            viewModel.username.value,
                            viewModel.password.value
                        )
                    },
                    onFailure = { e ->
                        Toast.makeText(
                            context,
                            e.localizedMessage ?: "Something wrong happened",
                            Toast.LENGTH_LONG
                        ).show()
                        Timber.e(e)
                    },
                    onLoading = {
                        Toast.makeText(context, "Signing in...", Toast.LENGTH_LONG).show()
                    }
                )
                it?.let { viewModel.loginFinished() }
            }
        }

        downloadJob = lifecycleScope.launch {
            viewModel.download.collect {
                it?.doOnFailure { e ->
                    Toast.makeText(
                        context,
                        e.localizedMessage ?: "Something wrong happened",
                        Toast.LENGTH_LONG
                    ).show()
                    Timber.e(e)
                }
                it?.let { viewModel.downloadFinished() }
            }
        }
    }

    override fun onStop() {
        loginJob = null
        downloadJob = null
        loginSettingsJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
