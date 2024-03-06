package com.example.quizapp.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizapp.databinding.FragmentNetworkControllerBinding



class NetworkController : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (InternetConnection.isOnline(requireContext())) {
            val entryDirections = NetworkControllerDirections.actionNetworkControllerToEntry()
            findNavController().navigate(entryDirections)
        }
    }
    private var _binding: FragmentNetworkControllerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNetworkControllerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.networkAnimation.isIndeterminate = true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


