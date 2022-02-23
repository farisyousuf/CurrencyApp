package com.faris.currency.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.faris.currency.databinding.FragmentConverterBinding
import com.faris.domain.entity.response.currency.CurrencyEntity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ConverterFragment : Fragment() {
    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ConverterViewModel::class.java]
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.currencyList.observe(this) {
            binding.spFromCurrency.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                it.map { currency ->
                    currency.code
                }
            )
            binding.spFromCurrency.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.currencyList.value?.let { currencyList ->
                            viewModel.setFromCurrency(currencyList[position])
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //No Implementation Here
                    }
                }
            binding.spToCurrency.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                it.map { currency ->
                    currency.code
                }
            )
            binding.spToCurrency.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.currencyList.value?.let { currencyList ->
                            viewModel.setToCurrency(currencyList[position])
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //No Implementation Here
                    }
                }
        }

        viewModel.fromCurrency.observe(this) {
            it?.let {
                Toast.makeText(requireContext(), "${it.code} ${it.value}", Toast.LENGTH_LONG).show()
            }
        }
        viewModel.toCurrency.observe(this) {
            it?.let {
                Toast.makeText(requireContext(), "${it.code} ${it.value}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}