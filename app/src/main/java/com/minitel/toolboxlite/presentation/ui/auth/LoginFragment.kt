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
import com.minitel.toolboxlite.data.datastore.IcsReference
import com.minitel.toolboxlite.data.datastore.icsReferenceDataStore
import com.minitel.toolboxlite.data.datastore.loginSettingsDataStore
import com.minitel.toolboxlite.data.datastore.update
import com.minitel.toolboxlite.databinding.FragmentLoginBinding
import com.minitel.toolboxlite.domain.services.EmseAuthService
import com.minitel.toolboxlite.presentation.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var emseAuthService: EmseAuthService

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private var loginJob: Job? = null
    private var downloadJob: Job? = null
    private var loginSettingsJob: Job? = null
    private var icsReferenceJob: Job? = null

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

        lifecycleScope.launch {
            viewModel.isEmseLoggedIn.value = emseAuthService.isSignedIn()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        loginJob = lifecycleScope.launch {
            viewModel.login.collect {
                it?.fold(
                    onSuccess = {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        requireContext().loginSettingsDataStore.update(
                            viewModel.rememberMe.value,
                            viewModel.username.value,
                            viewModel.password.value
                        )
                        val path = emseAuthService.findIcs()
                        viewModel.isEmseLoggedIn.value = true
                        requireContext().icsReferenceDataStore.updateData {
                            IcsReference.newBuilder()
                                .setUsername(viewModel.username.value)
                                .setPath(path)
                                .build()
                        }
                    },
                    onFailure = { e ->
                        Toast.makeText(
                            requireContext(),
                            e.localizedMessage ?: "Something wrong happened",
                            Toast.LENGTH_LONG
                        ).show()
                        Timber.e(e)
                        viewModel.isEmseLoggedIn.value = false
                    },
                    onLoading = {
                        Toast.makeText(requireContext(), "Signing in...", Toast.LENGTH_SHORT).show()
                    }
                )
                it?.let { viewModel.loginFinished() }
            }
        }

        downloadJob = lifecycleScope.launch {
            viewModel.download.collect {
                it?.doOnFailure { e ->
                    Toast.makeText(
                        requireContext(),
                        e.localizedMessage ?: "Something wrong happened",
                        Toast.LENGTH_LONG
                    ).show()
                    Timber.e(e)
                }
                it?.let { viewModel.downloadFinished() }
            }
        }

        icsReferenceJob = lifecycleScope.launch {
            requireContext().icsReferenceDataStore.data.collect {
                if (it.path.isNotBlank()) {
                    viewModel.isIcsSaved.value = true
                    viewModel.icsUrl.value = it.path
                    viewModel.doDownload(it.path)
                }
            }
        }
    }

    override fun onStop() {
        loginJob = null
        downloadJob = null
        loginSettingsJob = null
        icsReferenceJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
