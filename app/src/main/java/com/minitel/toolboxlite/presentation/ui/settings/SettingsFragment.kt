package com.minitel.toolboxlite.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.minitel.toolboxlite.data.datastore.calendarSettingsDataStore
import com.minitel.toolboxlite.data.datastore.update
import com.minitel.toolboxlite.databinding.FragmentSettingsBinding
import com.minitel.toolboxlite.presentation.viewmodels.SettingsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private val viewModel by viewModels<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    private var earlyMinutesJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        earlyMinutesJob = lifecycleScope.launch {
            viewModel.earlyMinutes.collect { value ->
                requireContext().calendarSettingsDataStore.update(value)
            }
        }
    }

    override fun onStop() {
        earlyMinutesJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
