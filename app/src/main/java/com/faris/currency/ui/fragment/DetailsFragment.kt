package com.faris.currency.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.faris.currency.databinding.FragmentDetailsBinding
import com.faris.currency.ui.models.RateItemViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getData(args.fromCurrency, args.toCurrency)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        subscribeToEvents()
        observeItems()
    }

    private fun observeItems() {
        viewModel.chartItems.observe(viewLifecycleOwner) {
            updateChart(it)
        }
    }

    private fun updateChart(ratesResponseList: ArrayList<RateItemViewModel>) {
        lifecycleScope.launch(Dispatchers.Default) {
            val entries = mutableListOf<Entry>()

            for (ratesResponse in ratesResponseList) {
                entries.add(
                    Entry(
                        entries.count().toFloat(),
                        ratesResponse.rate?.toFloat() ?: 0f,
                        args.toCurrency
                    )
                )
            }
            val dataSet = LineDataSet(entries, args.toCurrency)
            val lineData = LineData(dataSet)

            withContext(Dispatchers.Main) {
                binding.chart.data = lineData
                binding.chart.invalidate()
            }
        }
    }

    private fun subscribeToEvents() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), "${it.errorMessage}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loadingEvent.observe(viewLifecycleOwner) {
            it?.let { loading ->
                if (loading) {
                    binding.progressBar.visibility = View.VISIBLE
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                } else {
                    binding.progressBar.visibility = View.GONE
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }
}