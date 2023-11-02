package com.example.submissionawalstoryapp.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionawalstoryapp.data.preferences.LoginPreference
import com.example.submissionawalstoryapp.data.remote.APIConfig
import com.example.submissionawalstoryapp.data.response.UploadStory
import com.example.submissionawalstoryapp.utils.Helper.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun postCreateStory(imageFile: File, desc: String) {
        _isLoading.value = true

        val file = reduceFileImage(imageFile)

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val token = LoginPreference(context).getUser().token

        val client = token?.let {
            APIConfig.getAPIService().uploadStory(
                token = "Bearer $it",
                file = imageMultipart,
                description = description,
            )
        }

        client?.enqueue(object : Callback<UploadStory> {
            override fun onResponse(
                call: Call<UploadStory>,
                response: Response<UploadStory>
            ) {
                if (response.body()?.error == false) {
                    _isSuccess.value = true
                    _isLoading.value = false
                    _isError.value = false
                } else {
                    _isError.value = true
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<UploadStory>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}
