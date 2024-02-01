package com.example.quizapp.screen.play

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quizapp.data.QuestionCodable
import com.example.quizapp.R
import com.example.quizapp.data.Voice
import com.example.quizapp.databinding.FragmentPlayBinding
import com.example.quizapp.screen.network.InternetConnection
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random


class Play : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var playViewModel: PlayViewModel
    private lateinit var correctAnswer: String
    private lateinit var answerButtons: Array<Button>
    private lateinit var voice: Voice
    private var currentQuestionIndex: Int = Random.nextInt(0, 598)
    private val askedQuestions = HashSet<Int>()
    private lateinit var sharedPref: SharedPreferences
    private var mRewardedAd: RewardedAd? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voice = Voice(requireContext(), requireActivity())
        playViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[PlayViewModel::class.java]

        playViewModel.questions.observe(viewLifecycleOwner) {
            initializeGame(it!!)
        }
        playViewModel.progress.observe(viewLifecycleOwner) {
            binding.playProgressBar.progress = it
            binding.playFragmentProgressBarTextView.text = it.toString()
            backToEntry(it)
        }
        sharedPref = requireActivity()?.getPreferences(Context.MODE_PRIVATE) ?: return
        initAnswerButtons()
        setBackButtonClickListener()
        playViewModel.startCountdownTimer()
        MobileAds.initialize(requireContext()) { initStatus ->

        }


        // Rastgele reklam yüklemek için zamanlayıcı başlat
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
              showRewardedAd()
            }
        }, 0, 30000) // 30 saniye (30000 milisaniye) aralıklarla reklamı yükle
    }


    private fun loadRewardedAdd() {
        RewardedAd.load(
            requireContext(),
            "ca-app-pub-5990820577037460/8062547564",
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(addError: LoadAdError) {
                    super.onAdFailedToLoad(addError)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd = rewardedAd
                }
            })
    }

    private fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    loadRewardedAdd()
                }

                override fun onAdFailedToShowFullScreenContent(addError: AdError) {
                    super.onAdFailedToShowFullScreenContent(addError)
                    mRewardedAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }
            }
            mRewardedAd!!.show(requireActivity()) {

            }
        }

    }

    private fun updateUIWithCurrentQuestion(index: Int, questions: List<QuestionCodable>) {
        binding.playQuestionsText.text = questions[index].question
        binding.playCategoryText.text = questions[index].category
        correctAnswer = questions[index].correctAnswer.toString()
        val answers = questions[index].anwers?.split(",")?.shuffled()
        answers?.forEachIndexed { i, answer -> answerButtons[i].text = answer }
    }

    private fun handleButtonCorrectAnswer() {
        do {
            currentQuestionIndex = Random.nextInt(0, 598)
        } while (askedQuestions.contains(currentQuestionIndex))

        askedQuestions.add(currentQuestionIndex)

        val questions = playViewModel.questions.value
        if (currentQuestionIndex < questions!!.size) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                updateUIWithCurrentQuestion(currentQuestionIndex, questions)
                resetAnswerButtonsColors()


            }
        }
    }

    private fun resetAnswerButtonsColors() {
        for (button in answerButtons) {
            button.setBackgroundColor(resetButtonBackgroundColor())
            button.isClickable = true
        }
    }

    private fun setAnswerButtonsClickable(clickable: Boolean) {
        for (button in answerButtons) {
            button.isClickable = clickable
        }
    }

    private fun processCurrentAnswer(index: Int) {
        if (answerButtons[index].text == correctAnswer) {
            answerButtons[index].setBackgroundColor(correctAnswerTrueColor())
            handleButtonCorrectAnswer()
            trueVoice()
            setAnswerButtonsClickable(false)
            playViewModel.cancelCountdownTimer()
            playViewModel.startCountdownTimer()


        } else {
            answerButtons[index].setBackgroundColor(correctAnswerFalseColor())
            setBackButtonClickListener()
            playViewModel.cancelCountdownTimer()
            playViewModel.startCountdownTimer()
            currentAnswer()
            falseVoice()
            handleButtonCorrectAnswer()

        }
    }

    private fun currentAnswer() {
        for (button in answerButtons) {
            if (button.text == correctAnswer) {
                button.setBackgroundColor(correctAnswerTrueAwaitColor())
            }
        }
    }

    private fun setAnswerButtonsListeners() {
        for ((index, button) in answerButtons.withIndex()) {
            button.setOnClickListener {
                onAnswerButtonClick(index)
                setAnswerButtonsClickable(false)
                CoroutineScope(Dispatchers.Main).launch { delay(500); processCurrentAnswer(index) }
            }
        }
    }

    private fun onAnswerButtonClick(index: Int) {
        answerButtons[index].isClickable = false
        CoroutineScope(Dispatchers.Main).launch {
            answerButtons[index].setBackgroundColor(buttonClickAwaitColor())
            delay(500)
        }
    }

    private fun initializeGame(questions: List<QuestionCodable>) {
        updateUIWithCurrentQuestion(currentQuestionIndex, questions)
        setAnswerButtonsListeners()
    }

    private fun backToNavigate() {
        findNavController().navigateUp()
    }

    private fun backToEntry(index: Int) {
        if (index == 0) {
            timeFinish()
            playViewModel.cancelCountdownTimer()
            timeFinishCount()
            timerFinishVoice()
        }
    }

    private fun timeFinish() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            handleButtonCorrectAnswer()
            timeFinishCorrectAnswer()

        }
    }

    private fun timeFinishCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1500)
            playViewModel.startCountdownTimer()
            playViewModel.getCachedQuestions()
            playViewModel.fetchQuestionsFromFirebase()
        }
    }

    private fun timeFinishCorrectAnswer() {
        for (button in answerButtons) {
            if (button.text == correctAnswer) {
                button.setBackgroundColor(correctAnswerTrueColor())
            }
        }
    }

    private fun setBackButtonClickListener() {
        binding.playBackButton.setOnClickListener {
            backToNavigate()

        }
    }

    private fun initAnswerButtons() {
        answerButtons = arrayOf(
            binding.playQuestionsFirstButton,
            binding.playQuestionsSecondButton,
            binding.playQuestionsThirdButton,
            binding.playQuestionsFourthButton,
            binding.playQuestionsFifthButton
        )
    }

    private fun correctAnswerTrueColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.green)
    }

    private fun correctAnswerTrueAwaitColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.blue)
    }

    private fun correctAnswerFalseColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.red)
    }

    private fun resetButtonBackgroundColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.white)
    }

    private fun buttonClickAwaitColor(): Int {
        return ContextCompat.getColor(requireContext(), R.color.ic_launcher_background)
    }

    private fun trueVoice() {
        val trueId = R.raw.current_true
        val trueMediaPlayer = MediaPlayer.create(requireContext(), trueId)

        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled)
            trueMediaPlayer.start()
        else {
            trueMediaPlayer.pause()
            trueMediaPlayer.release()
        }
    }

    private fun falseVoice() {
        val falseId = R.raw.current_false
        val falseMediaPlayer = MediaPlayer.create(requireContext(), falseId)

        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled)
            falseMediaPlayer.start()
        else {
            falseMediaPlayer.pause()
            falseMediaPlayer.release()
        }
    }


    private fun timerFinishVoice() {
        val timerId = R.raw.timer_finish
        val timerFinish = MediaPlayer.create(requireContext(), timerId)

        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled)
            timerFinish.start()
        else {
            timerFinish.stop()
            timerFinish.release()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


}
