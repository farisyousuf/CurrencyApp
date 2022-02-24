package com.faris.currency.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.faris.currency.databinding.FragmentConverterBinding
import com.faris.currency.util.extensions.onImeActionDone
import com.faris.currency.util.extensions.onItemSelected
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConverterFragment : Fragment() {
    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConverterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCurrencyList()
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
        prepareBindingListeners()
        subscribeToObservers()
        subscribeToEvents()
    }

    private fun prepareBindingListeners() {
        binding.etFrom.onImeActionDone(requireActivity()) { view ->
            view?.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                viewModel.convert(it)
            }
        }

        binding.etTo.onImeActionDone(requireActivity()) { view ->
            view?.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                viewModel.convert(it, true)
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.currencyList.observe(viewLifecycleOwner) {
            binding.spFromCurrency.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                it.map { currency ->
                    currency.code
                }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.spFromCurrency.onItemSelected { position ->
                viewModel.currencyList.value?.let { currencyList ->
                    val selectedCurrency = currencyList[position]
                    viewModel.setFromCurrency(selectedCurrency)
                }
            }
            binding.spToCurrency.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                it.map { currency ->
                    currency.code
                }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            setDefaultCurrencyToFrom()

            binding.spToCurrency.onItemSelected { position ->
                viewModel.currencyList.value?.let { currencyList ->
                    val selectedCurrency = currencyList[position]
                    viewModel.setToCurrency(selectedCurrency)
                }
            }
        }

        viewModel.fromCurrency.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.convert(binding.etFrom.text.toString())
            }
        }
        viewModel.toCurrency.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.convert(binding.etFrom.text.toString())
            }
        }

        viewModel.fromAmount.observe(viewLifecycleOwner) {
            it?.let {
                binding.etFrom.setText(it)
            }
        }
        viewModel.toAmount.observe(viewLifecycleOwner) {
            it?.let {
                binding.etTo.setText(it)
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

        viewModel.switchAmount.observe(viewLifecycleOwner) {
            val fromAmount = binding.etFrom.text.toString()
            val toAmount = binding.etTo.text.toString()
            viewModel.setFromAmount(toAmount)
            viewModel.setToAmount(fromAmount)
            //Here, the amount in to field will be
            //populated tp from, so we use toAmount
            viewModel.convert(toAmount)
        }

        viewModel.goToDetailsScreen.observe(viewLifecycleOwner) {
            if (viewModel.fromCurrency.value?.code != DEFAULT_CURRENCY) {
                Toast.makeText(
                    requireContext(),
                    "Details only available when From currency is $DEFAULT_CURRENCY",
                    Toast.LENGTH_LONG
                ).show()
                return@observe
            }
            findNavController().navigate(
                ConverterFragmentDirections.actionConverterFragmentToDetailsFragment(
                    fromCurrency = viewModel.fromCurrency.value?.code ?: "",
                    toCurrency = viewModel.toCurrency.value?.code ?: ""
                )
            )
        }
    }

    private fun setDefaultCurrencyToFrom() {
        val defaultPosition =
            viewModel.currencyList.value?.indexOfFirst { currency -> currency.code == DEFAULT_CURRENCY }
                .takeIf { index -> index != -1 }
        defaultPosition?.let {
            binding.spFromCurrency.setSelection(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEFAULT_CURRENCY = "EUR"
    }
}