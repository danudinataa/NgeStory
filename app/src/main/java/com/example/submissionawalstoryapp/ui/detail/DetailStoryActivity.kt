package com.example.submissionawalstoryapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import com.bumptech.glide.Glide
import com.example.submissionawalstoryapp.data.response.Story
import com.example.submissionawalstoryapp.databinding.ActivityDetailStoryBinding
import com.example.submissionawalstoryapp.utils.Constants
import com.example.submissionawalstoryapp.utils.Helper.withDateFormat

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = getParcelableExtra(intent, Constants.DETAIL_STORY, Story::class.java) as Story

        with(binding) {
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.photoUrl)
                .fitCenter()
                .into(imgStory)

            tvUsername.text = detailStory.name
            tvDate.text = detailStory.createdAt.withDateFormat()
            tvDescription.text = detailStory.description
        }
    }
}