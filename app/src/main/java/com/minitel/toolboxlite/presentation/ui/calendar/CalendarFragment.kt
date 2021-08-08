package com.minitel.toolboxlite.presentation.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.minitel.toolboxlite.core.state.doOnFailure
import com.minitel.toolboxlite.databinding.FragmentCalendarBinding
import com.minitel.toolboxlite.presentation.adapters.MonthListAdapter
import com.minitel.toolboxlite.presentation.viewmodels.CalendarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private val viewModel by viewModels<CalendarViewModel>()

    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding
        get() = _binding!!

    private var downloadJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.recyclerViewMonths.adapter = MonthListAdapter()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        downloadJob = lifecycleScope.launch {
            viewModel.download.collect {
                it?.doOnFailure { e ->
                    Toast.makeText(context, e.localizedMessage ?: "Something wrong happened", Toast.LENGTH_LONG).show()
                    Timber.e(e)
                }
                it?.let { viewModel.downloadFinished() }
            }
        }
    }

    override fun onStop() {
        downloadJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
