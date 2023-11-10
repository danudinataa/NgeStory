package com.example.submissionawalstoryapp.utils

import com.example.submissionawalstoryapp.data.response.ListStoryDetail
import com.example.submissionawalstoryapp.data.response.Login
import com.example.submissionawalstoryapp.data.response.LoginDataAccount
import com.example.submissionawalstoryapp.data.response.LoginResult
import com.example.submissionawalstoryapp.data.response.RegisterDataAccount

object DataDummy {

    fun generateDummyNewsEntity(): List<ListStoryDetail> {
        val newsList = arrayListOf<ListStoryDetail>()
        for (i in 0..100) {
            val stories = ListStoryDetail(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }

    fun generateDummyNewStories(): List<ListStoryDetail> {
        val newsList = arrayListOf<ListStoryDetail>()
        for (i in 0..100) {
            val stories = ListStoryDetail(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }

    fun generateDummyRequestLogin(): LoginDataAccount {
        return LoginDataAccount("testing123@gmail.com", "test1234")
    }

    fun generateDummyResponseLogin(): Login {
        val newLoginResult = LoginResult("This Testing", "testing", "this-is-token")
        return Login(newLoginResult, false, "Login successfully")
    }

    fun generateDummyRequestRegister(): RegisterDataAccount {
        return RegisterDataAccount("This Testing", "test123@gmail.com", "testing123")
    }

}