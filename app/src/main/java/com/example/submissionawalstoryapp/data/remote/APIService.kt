package com.example.submissionawalstoryapp.data.remote

import com.example.submissionawalstoryapp.data.response.ListStory
import com.example.submissionawalstoryapp.data.response.Login
import com.example.submissionawalstoryapp.data.response.Register
import com.example.submissionawalstoryapp.data.response.UploadStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {
    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Login>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Register>

    @GET("stories")
    fun getStoryList(
        @Header("Authorization") token:String
    ): Call<ListStory>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token:String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadStory>
}