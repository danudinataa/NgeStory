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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.viewmodel.AddStoryViewModel
import com.example.submissionawalstoryapp.databinding.ActivityAddStoryBinding
import com.example.submissionawalstoryapp.ui.customview.CustomDialog
import com.example.submissionawalstoryapp.utils.Constants
import com.example.submissionawalstoryapp.utils.Helper.createCustomTempFile
import com.example.submissionawalstoryapp.utils.Helper.uriToFile
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val addStoryViewModel by viewModels<AddStoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        observeViewModel()
    }

    private fun initializeViews() {
        with(binding) {
            btnGallery.setOnClickListener { chooseFromGallery() }
            btnCamera.setOnClickListener { takePhotoFromCamera() }
            btnSubmit.setOnClickListener { submitStory() }
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

    private fun submitStory() {
        val description = binding.etDescription.text.toString()
        if (description.isNotEmpty() && getFile != null) {
            addStoryViewModel.postCreateStory(getFile!!, description)
        } else {
            showErrorMessageOrEmptyDialog()
        }
    }

    private fun observeViewModel() {
        addStoryViewModel.isLoading.observe(this) { isLoading ->
            handleLoadingAndErrorStates(isLoading)
        }
    }

    private fun showErrorMessageOrEmptyDialog() {
        val errorStringRes = if (getFile == null) R.string.empty_image_description else R.string.error_server
        val animationRes = if (getFile == null) R.raw.error_anim else R.raw.error_anim
        CustomDialog(this, getString(errorStringRes), animationRes).show()
    }

    private fun handleLoadingAndErrorStates(isLoading: Boolean) {
        val isError = addStoryViewModel.isError.value ?: false

        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.layoutAddStory.visibility = if (isLoading) View.GONE else View.VISIBLE

        when {
            isError -> CustomDialog(this, getString(R.string.error_server), R.raw.error_anim).show()
            !isError && !isLoading -> showSuccessDialog()
        }
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
