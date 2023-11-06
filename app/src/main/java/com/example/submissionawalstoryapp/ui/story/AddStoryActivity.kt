package com.example.submissionawalstoryapp.ui.story

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.viewmodel.DataStoreViewModel
import com.example.submissionawalstoryapp.data.viewmodel.MainViewModel
import com.example.submissionawalstoryapp.data.viewmodel.MainViewModelFactory
import com.example.submissionawalstoryapp.data.viewmodel.ViewModelFactory
import com.example.submissionawalstoryapp.databinding.ActivityAddStoryBinding
import com.example.submissionawalstoryapp.ui.customview.CustomDialog
import com.example.submissionawalstoryapp.ui.home.dataStore
import com.example.submissionawalstoryapp.ui.maps.PickLocationActivity
import com.example.submissionawalstoryapp.utils.Constants
import com.example.submissionawalstoryapp.utils.Helper.createCustomTempFile
import com.example.submissionawalstoryapp.utils.Helper.uriToFile
import com.example.submissionawalstoryapp.utils.UserPreferences
import com.google.android.gms.maps.model.LatLng
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var token: String
    private var getFile: File? = null
    private lateinit var fileFinal: File
    private var latlng: LatLng? = null
    private val addStoryViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = UserPreferences.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]

        dataStoreViewModel.getToken().observe(this) {
            token = it
        }

        initializeViews()
        observeViewModel()
    }

    private fun initializeViews() {
        with(binding) {
            btnGallery.setOnClickListener { chooseFromGallery() }
            btnCamera.setOnClickListener { takePhotoFromCamera() }
            btnSubmit.setOnClickListener { submitStory() }
            llDetailLocation.setOnClickListener { chooseLocation() }
        }
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissionsToCheck = Constants.REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToCheck.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToCheck.toTypedArray(),
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val resultBitmap = BitmapFactory.decodeFile(myFile.path)
            binding.imgPicker.setImageBitmap(resultBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri? = result.data?.data
            selectedImg?.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.imgPicker.setImageURI(uri)
            }
        }
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(applicationContext).also {
            val photoURI = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.submissionawalstoryapp.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val address = data.getStringExtra("address")
                    val lat = data.getDoubleExtra("lat", 0.0)
                    val lng = data.getDoubleExtra("lng", 0.0)
                    latlng = LatLng(lat, lng)

                    binding.tvDetailLocation.text = address
                }
            }
        }

    private fun submitStory() {

        val description = binding.etDescription.text.toString().trim()

        if (description.isEmpty() && getFile == null) {
            showErrorMessageDialog(R.string.empty_image_description, R.raw.error_anim)
        } else if (getFile == null) {
            showErrorMessageDialog(R.string.empty_image, R.raw.error_anim)
        } else if (description.isEmpty()) {
            showErrorMessageDialog(R.string.empty_description, R.raw.error_anim)
        } else {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val file = getFile as File
                    var compressedFile: File? = null
                    var compressedFileSize = file.length()

                    while (compressedFileSize > 1 * 1024 * 1024) {
                        compressedFile = withContext(Dispatchers.Default) {
                            Compressor.compress(applicationContext, file)
                        }
                        compressedFileSize = compressedFile.length()
                    }
                    fileFinal = compressedFile ?: file
                }

                val requestImageFile =
                    fileFinal.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    fileFinal.name,
                    requestImageFile
                )
                val desPart = description.toRequestBody("text/plain".toMediaType())

                addStoryViewModel.postCreateStory(
                    imageMultipart,
                    desPart,
                    latlng?.latitude,
                    latlng?.longitude,
                    token)
            }
        }
    }

    private fun chooseLocation() {
        val intent = Intent(this, PickLocationActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun observeViewModel() {
        addStoryViewModel.isLoading.observe(this) { isLoading ->
            handleLoadingAndErrorStates(isLoading)
        }
        addStoryViewModel.isError.observe(this) { isError ->
            if (isError) {
                showErrorMessageDialog(R.string.error_server, R.raw.error_anim)
            }
        }
        addStoryViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                showSuccessDialog()
            }
        }
    }

    private fun showErrorMessageDialog(errorMessageRes: Int, animationRes: Int) {
        CustomDialog(this, getString(errorMessageRes), animationRes).show()
    }


    private fun handleLoadingAndErrorStates(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.imgPicker.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showSuccessDialog() {
        CustomDialog(this, getString(R.string.success_upload_image), R.raw.success_anim) {
            resetViews()
            finish()
        }.show()
    }

    private fun resetViews() {
        binding.imgPicker.setAnimation(R.raw.add_image_anim)
        binding.etDescription.text?.clear()
    }
}
