package com.example.furnitureshoppingapp.fragment.loginRegister

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.databinding.FragmentIntroductionBinding


class IntroductionFragment : Fragment() {
    private lateinit var binding: FragmentIntroductionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGetStarted.setOnClickListener {
            val action = IntroductionFragmentDirections.actionIntroductionFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }
}