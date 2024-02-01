package com.example.quizapp.screen.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizapp.data.Voice
import com.example.quizapp.databinding.FragmentSettingsBinding
import com.example.quizapp.screen.network.InternetConnection
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class Settings : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var voice: Voice
    lateinit var mAdView: AdView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext())
        mAdView = binding.settingsFragmentadView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voice = Voice(requireContext(), requireActivity())
        backButton()
        switchOnVoice()


    }


    private fun switchOnVoice() {
        binding.settingsVoice.setOnCheckedChangeListener { _, isChecked ->
            updateVoiceSetting(isChecked)

            val sharedPref = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("voice_setting", isChecked)
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
            settingsToEntry()

        }
    }

    private fun settingsToEntry() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}