package com.example.submissionawalstoryapp.data.remote

import com.example.submissionawalstoryapp.data.response.LocationStory
import com.example.submissionawalstoryapp.data.response.Login
import com.example.submissionawalstoryapp.data.response.LoginDataAccount
import com.example.submissionawalstoryapp.data.response.PagingStory
import com.example.submissionawalstoryapp.data.response.Register
import com.example.submissionawalstoryapp.data.response.RegisterDataAccount
import com.example.submissionawalstoryapp.data.response.UploadStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface APIService {
    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Login>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<UploadStory>

    @GET("stories")
    suspend fun getPagingStory(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): PagingStory

    @GET("stories")
    fun getLocationStory(
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): Call<LocationStory>
    @POST("register")
    fun registUser(@Body requestRegister: RegisterDataAccount): Call<Register>

    @POST("login")
    fun loginUser(@Body requestLogin: LoginDataAccount): Call<Login>
}