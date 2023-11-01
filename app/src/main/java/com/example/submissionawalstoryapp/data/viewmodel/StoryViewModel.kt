package com.example.submissionawalstoryapp.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionawalstoryapp.data.preferences.LoginPreference
import com.example.submissionawalstoryapp.data.remote.APIConfig
import com.example.submissionawalstoryapp.data.response.ListStory
import com.example.submissionawalstoryapp.data.response.Story
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _listStories = MutableLiveData<List<Story>>()
    val listStories: LiveData<List<Story>> = _listStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    init {
        getStories()
    }

    fun getStories() {
        val token = LoginPreference(context).getUser().token
        _isError.value = false
        _isLoading.value = true

        token?.let {
            APIConfig.getAPIService().getStoryList(token = "Bearer $it")
                .enqueue(object : Callback<ListStory> {
                    override fun onResponse(call: Call<ListStory>, response: Response<ListStory>) {
                        _isLoading.value = false
                        _isError.value = response.body()?.error != false

                        if (!isError.value!!) {
                            _listStories.value = response.body()?.listStory
                        }
                    }

                    override fun onFailure(call: Call<ListStory>, t: Throwable) {
                        _isLoading.value = false
                        _isError.value = true
                    }
                })
        }
    }
}
