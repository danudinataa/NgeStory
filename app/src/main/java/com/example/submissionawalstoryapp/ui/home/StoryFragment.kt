package com.example.submissionawalstoryapp.ui.home

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.response.Story
import com.example.submissionawalstoryapp.data.viewmodel.StoryViewModel
import com.example.submissionawalstoryapp.databinding.FragmentStoryBinding
import com.example.submissionawalstoryapp.ui.adapter.ListStoryAdapter
import com.example.submissionawalstoryapp.ui.customview.CustomDialog
import com.example.submissionawalstoryapp.ui.detail.DetailStoryActivity
import com.example.submissionawalstoryapp.ui.story.AddStoryActivity
import com.example.submissionawalstoryapp.utils.Constants

class StoryFragment : Fragment(), ListStoryAdapter.OnItemClickListener {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!
    private val storyViewModel: StoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        observeViewModel()
        binding.fabAddstory.setOnClickListener {
            val intent = Intent(binding.root.context, AddStoryActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        storyViewModel.getStories()
    }

    private fun observeViewModel() {
        storyViewModel.listStories.observe(viewLifecycleOwner) { stories ->
            if (stories.isEmpty()) {
                Toast.makeText(requireContext(), "Empty Story!", Toast.LENGTH_SHORT).show()
            } else {
                showStoriesList(stories)
            }
        }

        storyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        storyViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                CustomDialog(
                    requireContext(),
                    getString(R.string.error_fetch_data),
                    R.raw.error_anim).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvStory.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showStoriesList(stories: List<Story>) {
        val context = binding.root.context
        val storiesRv = binding.rvStory

        storiesRv.layoutManager = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(context, 2)
        } else {
            LinearLayoutManager(context)
        }

        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.adapter = ListStoryAdapter(stories).apply {
            listener = this@StoryFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: Story, sharedView: View) {
        val intent = Intent(requireContext(), DetailStoryActivity::class.java)
        intent.putExtra(Constants.DETAIL_STORY, item)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            sharedView,
            ViewCompat.getTransitionName(sharedView) ?: ""
        )

        startActivity(intent, options.toBundle())
    }
}
