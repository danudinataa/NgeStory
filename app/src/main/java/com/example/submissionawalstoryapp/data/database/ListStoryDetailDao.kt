package com.example.submissionawalstoryapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submissionawalstoryapp.data.response.ListStoryDetail

@Dao
interface ListStoryDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<ListStoryDetail>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, ListStoryDetail>

    @Query("SELECT * FROM stories")
    fun getAllListStories(): List<ListStoryDetail> //get all stories but in list

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}