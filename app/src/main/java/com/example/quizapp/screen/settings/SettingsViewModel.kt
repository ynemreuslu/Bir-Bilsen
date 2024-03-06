package com.example.quizapp.screen.settings

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.quizapp.data.Voice


class SettingsViewModel : ViewModel() {

    private lateinit var navController: NavController
    private lateinit var voice: Voice
    private lateinit var sharedPreferences: SharedPreferences


    fun init(
        context: Context,
        activity: Activity,
        navController: NavController,
        sharedPreferences: SharedPreferences
    ) {
        this.navController = navController
        this.sharedPreferences = sharedPreferences
        initVoice(context, activity)
    }

    private fun initVoice(context: Context, activity: Activity) {
        voice = Voice(context, activity)
        switchShared()
    }

    private fun switchShared() {
        val savedVoiceSetting = sharedPreferences.getBoolean("voice_setting", false)
    }

    fun navigateBack() {
        navController.popBackStack()
    }


}