package com.example.quizapp.screen.entry

import android.os.Bundle
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
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback



class Entry : Fragment() {
    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlayViewModel
    private var mInterstitialAd: InterstitialAd? = null

    private final var TAG = "EntryFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        loadBannerAdd()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[PlayViewModel::class.java]

        viewModel.fetchQuestionsFromFirebase()
        viewModel.getCachedQuestions()
        viewModel.loadQuestionsFromCacheOrFirebase()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonListeners()
    }

    private fun loadBannerAdd() {
        MobileAds.initialize(requireContext())
            val adRequest = AdRequest.Builder().build()
            binding.entryFragmentAdView.loadAd(adRequest)

    }


    private fun setButtonListeners() {
        binding.entryFragmentSettingsButton.setOnClickListener { navigateToSettingsFragment(); showInterstitial();loadAd() }
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

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-5990820577037460/1208845084",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    mInterstitialAd = p0

                }
            })
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        loadAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        mInterstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is dismissed.
                    }
                }
            mInterstitialAd?.show(requireActivity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
