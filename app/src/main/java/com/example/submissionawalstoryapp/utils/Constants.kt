package com.example.submissionawalstoryapp.utils

object Constants {

    enum class UserPreferences {
        UserUID, Username, UserEmail, UserToken, UserLastLogin
    }

    enum class StoryDetail {
        Username, ImageURL, Description, UploadTime
    }

    const val preferenceName = "User Settings"
    const val preferenceDefaultValue = "Unset"
    const val preferenceDefaultDate = "2010/01/01 00:00:00"

    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    const val SPLASH_SCREEN_TIMER: Long = 3000
    const val REQUEST_CODE_PERMISSIONS = 10
    val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    const val UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val CREATED_DATE_FORMAT = "dd-MMM-yyyy"
    const val UTC_TIME_ZONE = "UTC"
    const val DETAIL_STORY = "DETAIL_STORY"
    const val MIN_LENGTH_PASSWORD = 8
    const val SUFFIX_IMAGE_FILE = ".jpg"
    const val SIZE_BYTE_ARRAY = 1024
    const val STREAM_LENGTH = 1000000
}