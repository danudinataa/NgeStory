package com.example.submissionawalstoryapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionawalstoryapp.data.response.Story
import com.example.submissionawalstoryapp.databinding.ItemStoryLayoutBinding

class ListStoryAdapter(private val githubUserList: List<Story>)
    : RecyclerView.Adapter<ListStoryAdapter.CustomViewHolder>() {

    lateinit var listener: OnItemClickListener

    inner class CustomViewHolder(private val binding: ItemStoryLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindList(story: Story){
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .skipMemoryCache(true)
                .into(binding.imgStory)
            binding.tvUsername.text = story.name
            binding.tvDate.text = story.createdAt
            binding.tvDescription.text = story.description
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
            listener.onItemClicked(githubUserList[position])
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(item: Story)
    }
}
