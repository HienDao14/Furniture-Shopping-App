package com.example.furnitureshoppingapp.fragment.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.furnitureshoppingapp.databinding.FragmentAddAddressBinding
import com.example.furnitureshoppingapp.model.Address
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.util.Constants.showTopSnackbar
import com.example.furnitureshoppingapp.viewmodel.AddAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddAddressFragment : Fragment() {
    private lateinit var binding: FragmentAddAddressBinding
    private val viewModel by viewModels<AddAddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resources.Loading -> {
                    }
                    is Resources.Success -> {
                        findNavController().navigateUp()
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                showTopSnackbar(it, requireView(), resources)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveAddress.setOnClickListener {
            var addressTitle = "Home"
            if(binding.radioWork.isChecked){
                addressTitle = "Work Space"
            }
            val fullName = binding.edtName.text.toString()
            val phoneNumber = binding.edtPhone.text.toString()
            val addressInfo = binding.edtAddress.text.toString()
            val province = binding.edtCity.text.toString()
            val district = binding.edtDistrict.text.toString()
            val ward = binding.edtWard.text.toString()
            val additionalInfo = binding.edtAdditionalInfo.text.toString()
            val address = Address(addressTitle, fullName, phoneNumber, addressInfo, province, district, ward, additionalInfo)

            viewModel.addAddress(address)
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}