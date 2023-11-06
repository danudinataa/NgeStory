package com.example.submissionawalstoryapp.di

import android.content.Context
import com.example.submissionawalstoryapp.data.remote.APIConfig
import com.example.submissionawalstoryapp.data.database.StoryDatabase
import com.example.submissionawalstoryapp.data.repository.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = APIConfig.getAPIService()
        return MainRepository(database, apiService)
    }
}