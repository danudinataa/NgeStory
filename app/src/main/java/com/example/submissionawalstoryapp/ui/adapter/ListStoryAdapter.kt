package com.example.submissionawalstoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionawalstoryapp.data.database.ListStoryDetail
import com.example.submissionawalstoryapp.data.response.Story
import com.example.submissionawalstoryapp.databinding.ItemStoryLayoutBinding
import com.example.submissionawalstoryapp.utils.Helper.withDateFormat

class ListStoryAdapter(private val githubUserList: List<Story>)
    : PagingDataAdapter<ListStoryDetail, ListStoryAdapter.CustomViewHolder>(StoryDetailDiffCallback()) {

    lateinit var listener: OnItemClickListener

    inner class CustomViewHolder(val binding: ItemStoryLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindList(story: Story){
            ViewCompat.setTransitionName(binding.imgStory, "img_story_anim")
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .skipMemoryCache(true)
                .into(binding.imgStory)
            binding.tvUsername.text = story.name
            binding.tvDate.text = story.createdAt.withDateFormat()
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

    override fun getItemCount(): Int {
        return githubUserList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindList(githubUserList[position])
        holder.itemView.setOnClickListener {
            listener.onItemClicked(githubUserList[position], holder.binding.imgStory)
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(item: Story, sharedView: View)
    }
}
