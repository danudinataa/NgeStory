package com.example.submissionawalstoryapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import com.bumptech.glide.Glide
import com.example.submissionawalstoryapp.data.response.ListStoryDetail
import com.example.submissionawalstoryapp.databinding.ActivityDetailStoryBinding
import com.example.submissionawalstoryapp.utils.Constants
import com.example.submissionawalstoryapp.utils.Helper
import com.example.submissionawalstoryapp.utils.Helper.withDateFormat

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = getParcelableExtra(intent, Constants.DETAIL_STORY, ListStoryDetail::class.java) as ListStoryDetail

        with(binding) {
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.photoUrl)
                .fitCenter()
                .into(imgStory)

            tvUsername.text = detailStory.name
            tvDate.text = detailStory.createdAt?.withDateFormat()
            tvDescription.text = detailStory.description
            tvDetailLocation.text = Helper.getStringAddress(
                Helper.toLatlng(detailStory.lat, detailStory.lon),
                this@DetailStoryActivity
            )
        }
    }
}