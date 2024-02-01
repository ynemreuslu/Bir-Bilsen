package com.example.quizapp.screen.entry

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.quizapp.databinding.FragmentEntryBinding
import com.example.quizapp.screen.play.PlayViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class Entry : Fragment() {
    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlayViewModel
    lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[PlayViewModel::class.java]
        viewModel.fetchQuestionsFromFirebase()
        viewModel.getCachedQuestions()
        viewModel.loadQuestionsFromCacheOrFirebase()
        loadBannerAdd()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonListeners()
    }

    private fun loadBannerAdd() {
        MobileAds.initialize(requireContext())
        mAdView = binding.entryFragmentAdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }


    private fun setButtonListeners() {
        binding.entryFragmentSettingsButton.setOnClickListener { navigateToSettingsFragment() }
        binding.entryFragmentPlayButton.setOnClickListener { navigateToPlayFragment() }
    }

    private fun navigateToPlayFragment() {
        val playFragmentAction = EntryDirections.actionEntryToPlay()
        findNavController().navigate(playFragmentAction)
    }


    private fun navigateToSettingsFragment() {
        val settingsFragmentAction = EntryDirections.actionEntryToSettings()
        findNavController().navigate(settingsFragmentAction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
