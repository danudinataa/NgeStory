package com.example.submissionawalstoryapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.response.Story
import com.example.submissionawalstoryapp.databinding.ActivityDetailStoryBinding
import com.example.submissionawalstoryapp.utils.Constants

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<Story>(Constants.DETAIL_STORY) as Story

    }
}