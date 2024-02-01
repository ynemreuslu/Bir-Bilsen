package com.example.quizapp.data

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import com.example.quizapp.R

class Voice(private val context: Context, private val activity: Activity) {

    private var trueMediaPlayer: MediaPlayer? = null
    private var falseMediaPlayer: MediaPlayer? = null

    fun playTrueSound() {
        createPlayerIfNeeded(R.raw.current_true)
        if (!trueMediaPlayer?.isPlaying!!) {
            trueMediaPlayer?.start()
        }
    }

    fun stopTrueSound() {
        trueMediaPlayer?.stop()
        trueMediaPlayer?.release()
        trueMediaPlayer = null
    }

    private fun createPlayerIfNeeded(resourceId: Int) {
        if (trueMediaPlayer == null) {
            trueMediaPlayer = MediaPlayer.create(context, resourceId)
        }
    }

    fun toggleTrueSound() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled) {
            if (!trueMediaPlayer?.isPlaying!!) {
                playTrueSound()
            }
        } else {
            stopTrueSound()
        }
    }

    fun playFalseSound() {
        createPlayerIfNeeded(R.raw.current_false)
        if (!trueMediaPlayer?.isPlaying!!) {
            trueMediaPlayer?.start()
        }
    }

    fun stopFalseSound() {
        falseMediaPlayer?.stop()
        falseMediaPlayer?.release()
        falseMediaPlayer = null
    }

    private fun createPlayerFalseIfNeeded(resourceId: Int) {
        if (falseMediaPlayer == null) {
            falseMediaPlayer = MediaPlayer.create(context, resourceId)
        }
    }

    fun toggleFalseSound() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val isVoiceEnabled = sharedPref.getBoolean("voice", false)

        if (isVoiceEnabled) {
            if (!falseMediaPlayer?.isPlaying!!) {
                playTrueSound()
            }
        } else {
            stopFalseSound()
        }
    }

}
