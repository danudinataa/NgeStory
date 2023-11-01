package com.example.submissionawalstoryapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Patterns
import android.view.Gravity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Helper {
    private const val FILENAME_FORMAT = "dd-MMM-yyyy"

    val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

    fun createCustomTempFile(context: Context): File =
        File.createTempFile(timeStamp, Constants.SUFFIX_IMAGE_FILE, context.getExternalFilesDir(
            Environment.DIRECTORY_PICTURES))

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        context.contentResolver.openInputStream(selectedImg)?.use { inputStream ->
            FileOutputStream(myFile).use { outputStream ->
                val buf = ByteArray(Constants.SIZE_BYTE_ARRAY)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            }
        }
        return myFile
    }

    fun String.withDateFormat(): String {
        val formatter = SimpleDateFormat(Constants.UTC_FORMAT, Locale.US).apply { timeZone = TimeZone.getTimeZone(Constants.UTC_TIME_ZONE) }
        val value = formatter.parse(this) as Date
        return SimpleDateFormat(Constants.CREATED_DATE_FORMAT, Locale.US).apply { timeZone = TimeZone.getDefault() }.format(value)
    }

    fun reduceFileImage(file: File): File {
        var compressQuality = 100
        var streamLength: Int
        val bitmap = BitmapFactory.decodeFile(file.path)
        do {
            val bmpStream = ByteArrayOutputStream().apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, this)
            }
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > Constants.STREAM_LENGTH)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun isValidEmail(email: String): Boolean =
        !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun validateMinLength(password: String): Boolean =
        !TextUtils.isEmpty(password) && password.length >= Constants.MIN_LENGTH_PASSWORD

}
