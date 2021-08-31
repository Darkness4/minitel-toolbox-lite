package com.minitel.toolboxlite.presentation.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.minitel.toolboxlite.data.datastore.calendarSettingsDataStore
import com.minitel.toolboxlite.databinding.FragmentCalendarBinding
import com.minitel.toolboxlite.domain.services.IcsEventScheduler
import com.minitel.toolboxlite.presentation.adapters.MonthListAdapter
import com.minitel.toolboxlite.presentation.viewmodels.CalendarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private val viewModel by viewModels<CalendarViewModel>()

    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding
        get() = _binding!!

    @Inject
    lateinit var icsEventScheduler: IcsEventScheduler

    private var icsEventsJob: Job? = null

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
        icsEventsJob = lifecycleScope.launch {
            viewModel.events.collect { list ->
                if (list.isNotEmpty()) {
                    val earlyMinutes =
                        requireContext().calendarSettingsDataStore.data.firstOrNull()?.earlyMinutes
                            ?: 5L
                    list.forEach { icsEventScheduler.schedule(it, earlyMinutes) }
                    Toast.makeText(context, "Scheduled ${list.size} events.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onStop() {
        icsEventsJob?.cancel()
        icsEventsJob = null
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
