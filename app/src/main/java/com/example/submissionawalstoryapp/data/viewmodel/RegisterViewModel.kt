package com.example.submissionawalstoryapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submissionawalstoryapp.data.remote.APIConfig
import com.example.submissionawalstoryapp.data.response.Register
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        _isError.value = false
        _isSuccess.value = false

        val client = APIConfig.getAPIService().register(name, email, password)
        client.enqueue(object : Callback<Register> {
            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                _isLoading.value = false
                _isSuccess.value = response.body()?.error == false
                _isError.value = !response.isSuccessful || response.body()?.error != false
            }

            override fun onFailure(call: Call<Register>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}
