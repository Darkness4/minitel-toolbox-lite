package com.minitel.toolboxlite.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.minitel.toolboxlite.R
import com.minitel.toolboxlite.databinding.FragmentSettingsBinding
import com.minitel.toolboxlite.presentation.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel by viewModels<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    private var openLicencesJob: Job? = null
    private var openAboutJob: Job? = null

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
        openLicencesJob = lifecycleScope.launch {
            viewModel.openLicences.collect {
                it?.let {
                    startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    viewModel.openLicencesDone()
                }
            }
        }

        openAboutJob = lifecycleScope.launch {
            viewModel.openAbout.collect {
                it?.let {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.repository_url))
                        )
                    )
                    viewModel.openAboutDone()
                }
            }
        }
    }

    override fun onStop() {
        openLicencesJob?.cancel()
        openLicencesJob = null
        openAboutJob?.cancel()
        openAboutJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
