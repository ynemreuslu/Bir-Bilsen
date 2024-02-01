package com.example.quizapp.screen.play

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizapp.data.QuestionCodable

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PlayViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferencesName = "QuestionSharedPref"
    private val questionCacheKey = "QuestionCache"


    private val _questions = MutableLiveData<List<QuestionCodable>?>()
    val questions: LiveData<List<QuestionCodable>?> get() = _questions


    private val questionsDatabaseReference =
        Firebase.database.reference.child("1A3X2SpayfkhlIG1vsAjrh4dLgk85SH8yatZRHfK5lCE")
            .child("questions")


    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    private var countDownTimer: CountDownTimer? = null

    init {
        loadQuestionsFromCacheOrFirebase()

    }

     fun loadQuestionsFromCacheOrFirebase() {
        val cachedQuestions = getCachedQuestions()
        if (cachedQuestions != null) {
            _questions.value = cachedQuestions
        } else {
            fetchQuestionsFromFirebase()
        }
    }

    fun fetchQuestionsFromFirebase() {
        questionsDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val questionList = ArrayList<QuestionCodable>()
                for (questionSnapshot in dataSnapshot.children) {
                    val question = questionSnapshot.getValue(QuestionCodable::class.java)
                    question?.let { questionList.add(it) }
                }
                _questions.value = questionList
                cacheQuestions(questionList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PlayViewModel", error.message)
            }
        })
    }

    fun cacheQuestions(questionList: List<QuestionCodable>) {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences(
                sharedPreferencesName,
                Context.MODE_PRIVATE
            )
        with(sharedPreferences.edit()) {
            putString(questionCacheKey, Gson().toJson(questionList))
            apply()
        }
    }

     fun getCachedQuestions(): List<QuestionCodable>? {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences(
                sharedPreferencesName,
                Context.MODE_PRIVATE
            )
        val jsonString = sharedPreferences.getString(questionCacheKey, null)
        return jsonString?.let {
            Gson().fromJson(it, object : TypeToken<List<QuestionCodable>>() {}.type)
        }
    }

    fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(21000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _progress.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _progress.value = 0
            }
        }.start()
    }

    fun cancelCountdownTimer() {
        countDownTimer?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelCountdownTimer()
    }
}