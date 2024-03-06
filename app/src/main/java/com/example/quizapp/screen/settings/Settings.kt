package com.example.quizapp.screen.settings


import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizapp.databinding.FragmentSettingsBinding
import com.example.quizapp.network.InternetConnection
import com.example.quizapp.network.NetworkControllerDirections
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode


class Settings : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel'in başlatılması
        settingsViewModel.init(
            requireContext(),
            requireActivity(),
            findNavController(),
            requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        )
        sharedPreferences = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        appLinksButtons()
        initAdView()
        switchOnVoice()
        backButton()
    }

    private fun initAdView() {
        val adRequest = AdRequest.Builder().build()
        binding.settingsFragmentAd.loadAd(adRequest)
    }

    private fun switchOnVoice() {
        binding.settingsVoice.setOnCheckedChangeListener { _, isChecked ->
            updateVoiceSetting(isChecked)

            val sharedPref = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("voice_s" +
                        "etting", isChecked)
                commit()
            }
        }
        val sharedPref = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE)
        val savedVoiceSetting = sharedPref.getBoolean("voice_setting", false)
        binding.settingsVoice.isChecked = savedVoiceSetting

    }


    private fun switchShared() {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE)
        val savedVoiceSetting = sharedPref.getBoolean("voice_setting", false)
        binding.settingsVoice.isChecked = savedVoiceSetting
    }


    private fun updateVoiceSetting(isVoiceEnabled: Boolean) {
        val sharedPref = requireActivity()?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("voice", isVoiceEnabled)
            commit()
        }

    }


    private fun backButton() {
        binding.settingsBackButton.setOnClickListener {
            settingsViewModel.navigateBack()
        }
    }


    private fun sharedFriendsLinkApp() {
        binding.settingsShare.setOnClickListener {
            TODO()
        }
    }

    private fun appLinksButtons() {
        binding.settingsPoint.setOnClickListener {
            val manager = ReviewManagerFactory.create(requireContext())
            binding.settingsPoint.setOnClickListener {
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // We got the ReviewInfo object
                        val reviewInfo = task.result
                    } else {
                        // There was some problem, log or handle the error code.
                        @ReviewErrorCode val reviewErrorCode = (task.getException() as ReviewException).errorCode
                    }
                }
            }



        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}