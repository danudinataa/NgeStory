package com.example.submissionawalstoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionawalstoryapp.data.response.ListStoryDetail
import com.example.submissionawalstoryapp.databinding.ItemStoryLayoutBinding
import com.example.submissionawalstoryapp.utils.Helper.withDateFormat

class ListStoryAdapter
    : PagingDataAdapter<ListStoryDetail, ListStoryAdapter.CustomViewHolder>(StoryDetailDiffCallback()) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryDetail, sharedView: View)
    }

    inner class CustomViewHolder(val binding: ItemStoryLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindList(story: ListStoryDetail){
            ViewCompat.setTransitionName(binding.imgStory, "img_story_anim")
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .skipMemoryCache(true)
                .into(binding.imgStory)
            binding.tvUsername.text = story.name
            binding.tvDate.text = story.createdAt?.withDateFormat() ?: "-"
            binding.tvDescription.text = story.description
        }
    }

    class StoryDetailDiffCallback : DiffUtil.ItemCallback<ListStoryDetail>() {
        override fun areItemsTheSame(oldItem: ListStoryDetail, newItem: ListStoryDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ListStoryDetail,
            newItem: ListStoryDetail
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            ItemStoryLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        getItem(position)?.let { holder.bindList(it) }
        holder.itemView.setOnClickListener {
            getItem(position)?.let { it1 -> onItemClickCallback.onItemClicked(it1, holder.binding.imgStory) }
        }
    }
}
