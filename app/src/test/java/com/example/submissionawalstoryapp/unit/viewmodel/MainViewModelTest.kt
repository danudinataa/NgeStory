package com.example.submissionawalstoryapp.unit.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionawalstoryapp.data.repository.MainRepository
import com.example.submissionawalstoryapp.data.response.ListStoryDetail
import com.example.submissionawalstoryapp.data.response.Login
import com.example.submissionawalstoryapp.data.viewmodel.MainViewModel
import com.example.submissionawalstoryapp.ui.adapter.ListStoryAdapter
import com.example.submissionawalstoryapp.utils.DataDummy.generateDummyNewStories
import com.example.submissionawalstoryapp.utils.DataDummy.generateDummyRequestLogin
import com.example.submissionawalstoryapp.utils.DataDummy.generateDummyRequestRegister
import com.example.submissionawalstoryapp.utils.DataDummy.generateDummyResponseLogin
import com.example.submissionawalstoryapp.utils.MainDispatcherRule
import com.example.submissionawalstoryapp.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var mainRepository: MainRepository

    @Mock
    private var mockFile = File("fileName")

    // get story
    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `test getStory is working and Should Not Return Null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStories()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<ListStoryDetail>>()
        val token = "this is token"

        story.value = data
        `when`(mainRepository.getPagingStories(token)).thenReturn(story)

        val mainViewModel = MainViewModel(mainRepository)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)

        advanceUntilIdle()
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `when getStory is Empty Should Not return Null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val data = PagedTestDataSources.snapshot(listOf())
        val story = MutableLiveData<PagingData<ListStoryDetail>>()
        val token = "this is token"

        story.value = data
        `when`(mainRepository.getPagingStories(token)).thenReturn(story)

        val mainViewModel = MainViewModel(mainRepository)
        val actualData = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.StoryDetailDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)

        advanceUntilIdle()
        assertNotNull(differ.snapshot())
        assertTrue(differ.snapshot().isEmpty())
        print(differ.snapshot().size)
    }

    // upload
    @Test
    fun `when message upload should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "Story Uploaded"


        `when`(mainRepository.message).thenReturn(expectedRegisterMessage)

        val mainViewModel = MainViewModel(mainRepository)
        val actualRegisterMessage = mainViewModel.message.getOrAwaitValue()

        assertNotNull(actualRegisterMessage)
        Assert.assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when loading upload should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true


        `when`(mainRepository.isLoading).thenReturn(expectedLoadingData)

        val mainViewModel = MainViewModel(mainRepository)
        val actualLoading = mainViewModel.isLoading.getOrAwaitValue()

        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `test post create function is working`() {
        val expectedUploadMessage = MutableLiveData<String>()
        expectedUploadMessage.value = "Story Uploaded"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "this is description".toRequestBody("text/plain".toMediaType())
        val token = "this is token"
        val latlng = LatLng(1.1, 1.1)

        `when`(mainRepository.message).thenReturn(expectedUploadMessage)

        val mainViewModel = MainViewModel(mainRepository)
        mainViewModel.postCreateStory(imageMultipart, description, latlng.latitude, latlng.longitude, token)
        val actualUploadMessage = mainViewModel.message.getOrAwaitValue()
        assertNotNull(actualUploadMessage)
        assertEquals(expectedUploadMessage.value, actualUploadMessage)
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<ListStoryDetail>>>() {
        companion object {
            fun snapshot(items: List<ListStoryDetail>): PagingData<ListStoryDetail> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryDetail>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryDetail>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    // login
    @Test
    fun `when login message should return the right data and not null`() {
        val expectedLoginMessage = MutableLiveData<String>()
        expectedLoginMessage.value = "Login Successfully"


        `when`(mainRepository.message).thenReturn(expectedLoginMessage)

        val mainViewModel = MainViewModel(mainRepository)
        val actualMessage = mainViewModel.message.getOrAwaitValue()

        assertNotNull(actualMessage)
        assertEquals(expectedLoginMessage.value, actualMessage)
    }

    @Test
    fun `when login loading state should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        `when`(mainRepository.isLoading).thenReturn(expectedLoadingData)

        val mainViewModel = MainViewModel(mainRepository)
        val actualLoading = mainViewModel.isLoading.getOrAwaitValue()

        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `when login should return the right login user data and not null`() {
        val dummyResponselogin = generateDummyResponseLogin()

        val expectedLogin = MutableLiveData<Login>()
        expectedLogin.value = dummyResponselogin

        `when`(mainRepository.userlogin).thenReturn(expectedLogin)

        val mainViewModel = MainViewModel(mainRepository)
        val actualLoginResponse = mainViewModel.userlogin.getOrAwaitValue()

        assertNotNull(actualLoginResponse)
        assertEquals(expectedLogin.value, actualLoginResponse)
    }

    @Test
    fun `test getResponseLogin function is working`() {
        val dummyRequestLogin = generateDummyRequestLogin()
        val dummyResponseLogin = generateDummyResponseLogin()

        val expectedResponseLogin = MutableLiveData<Login>()
        expectedResponseLogin.value = dummyResponseLogin

        `when`(mainRepository.userlogin).thenReturn(expectedResponseLogin)

        val mainViewModel = MainViewModel(mainRepository)
        mainViewModel.login(dummyRequestLogin)

        val actualData = mainViewModel.userlogin.getOrAwaitValue()

        assertNotNull(expectedResponseLogin)
        assertEquals(expectedResponseLogin.value, actualData)
    }

    // register
    @Test
    fun `when register message should return the right data and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created"

        `when`(mainRepository.message).thenReturn(expectedRegisterMessage)

        val mainViewModel = MainViewModel(mainRepository)
        val actualRegisterMessage = mainViewModel.message.getOrAwaitValue()

        assertNotNull(actualRegisterMessage)
        assertEquals(expectedRegisterMessage.value, actualRegisterMessage)
    }

    @Test
    fun `when register loading state should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        `when`(mainRepository.isLoading).thenReturn(expectedLoadingData)

        val mainViewModel = MainViewModel(mainRepository)
        val actualLoading = mainViewModel.isLoading.getOrAwaitValue()

        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `test getResponseRegister function is working`() {
        val dummyRequestRegister = generateDummyRequestRegister()
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created"

        `when`(mainRepository.message).thenReturn(expectedRegisterMessage)

        val mainViewModel = MainViewModel(mainRepository)
        mainViewModel.register(dummyRequestRegister)
        val actualData = mainViewModel.message.getOrAwaitValue()

        assertNotNull(actualData)
        assertEquals(expectedRegisterMessage.value, actualData)
    }
}