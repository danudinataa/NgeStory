package com.example.submissionawalstoryapp.ui.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.response.ListStoryDetail
import com.example.submissionawalstoryapp.data.viewmodel.DataStoreViewModel
import com.example.submissionawalstoryapp.data.viewmodel.MainViewModel
import com.example.submissionawalstoryapp.data.viewmodel.MainViewModelFactory
import com.example.submissionawalstoryapp.data.viewmodel.ViewModelFactory
import com.example.submissionawalstoryapp.databinding.FragmentStoryBinding
import com.example.submissionawalstoryapp.ui.adapter.ListStoryAdapter
import com.example.submissionawalstoryapp.ui.adapter.LoadingStateAdapter
import com.example.submissionawalstoryapp.ui.customview.CustomDialog
import com.example.submissionawalstoryapp.ui.detail.DetailStoryActivity
import com.example.submissionawalstoryapp.ui.maps.MapsActivity
import com.example.submissionawalstoryapp.ui.story.AddStoryActivity
import com.example.submissionawalstoryapp.utils.Constants
import com.example.submissionawalstoryapp.utils.UserPreferences

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!
    private val pref by lazy {
        UserPreferences.getInstance(requireContext().dataStore)
    }
    private lateinit var token: String
    private val storyViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(requireContext()))[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(viewLifecycleOwner) {
            token = it
            setupStoryList(it)
        }

        observeViewModel()

        binding.fabAddstory.setOnClickListener {
            val intent = Intent(binding.root.context, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.fabMapstory.setOnClickListener{
            val intent = Intent(binding.root.context, MapsActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (::token.isInitialized) {
            storyViewModel.getStories(token)
        } else {
            CustomDialog(requireContext(), getString(R.string.error_fetch_data), R.raw.error_anim )
        }
    }

    private fun observeViewModel() {
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

    @OptIn(ExperimentalPagingApi::class)
    private fun setupStoryList(token: String) {
        val context = binding.root.context
        val storiesRv = binding.rvStory

        val adapter = ListStoryAdapter()
        storiesRv.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        storiesRv.layoutManager = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(context, 2)
        } else {
            LinearLayoutManager(context)
        }

        storiesRv.setHasFixedSize(true)

        storyViewModel.getPagingStories(token).observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryDetail, sharedView: View) {
                val intent = Intent(requireContext(), DetailStoryActivity::class.java)
                intent.putExtra(Constants.DETAIL_STORY, data)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    sharedView,
                    ViewCompat.getTransitionName(sharedView) ?: ""
                )

                startActivity(intent, options.toBundle())
            }
        })

        with(binding) {
            rvStory.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 30 && fabAddstory.isExtended && fabMapstory.isExtended) {
                        fabAddstory.shrink()
                        fabMapstory.shrink()
                    }
                    if (dy < -40 && !fabAddstory.isExtended && !fabMapstory.isExtended) {
                        fabAddstory.extend()
                        fabMapstory.extend()
                    }
                    if (!rvStory.canScrollVertically(-1)) {
                        fabAddstory.extend()
                        fabMapstory.extend()
                    }
                }
            })
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvStory.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
