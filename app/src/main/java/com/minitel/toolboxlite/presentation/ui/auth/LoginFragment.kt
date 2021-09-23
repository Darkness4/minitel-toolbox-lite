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
import com.minitel.toolboxlite.databinding.FragmentLoginBinding
import com.minitel.toolboxlite.domain.services.IcsEventScheduler
import com.minitel.toolboxlite.presentation.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var icsEventScheduler: IcsEventScheduler

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private var loginJob: Job? = null
    private var downloadJob: Job? = null
    private var icsEventsJob: Job? = null

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
                    onSuccess = {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        viewModel.updateLoginSettings()
                        viewModel.updateIcsReference()
                    },
                    onFailure = { e ->
                        Toast.makeText(
                            requireContext(),
                            e.localizedMessage ?: "Something wrong happened",
                            Toast.LENGTH_LONG
                        ).show()
                        logcat(LogPriority.ERROR) { e.asLog() }
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
                    logcat(LogPriority.ERROR) { e.asLog() }
                }
                it?.let { viewModel.downloadFinished() }
            }
        }

        icsEventsJob = lifecycleScope.launch {
            viewModel.calendarSettings.collect { calendarSettings ->
                calendarSettings?.let {
                    viewModel.events.collect { list ->
                        if (list.isNotEmpty()) {
                            withContext(Dispatchers.Default) {
                                list.forEach {
                                    icsEventScheduler.schedule(
                                        it,
                                        calendarSettings.earlyMinutes
                                    )
                                }
                            }
                            logcat(LogPriority.DEBUG) { "Scheduled ${list.size} events." }
                            Toast.makeText(
                                context,
                                "Scheduled ${list.size} events.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        loginJob?.cancel()
        loginJob = null
        downloadJob?.cancel()
        downloadJob = null
        icsEventsJob?.cancel()
        icsEventsJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
