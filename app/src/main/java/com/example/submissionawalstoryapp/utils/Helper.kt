package com.example.submissionawalstoryapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Helper {
    private const val FILENAME_FORMAT = "dd-MMM-yyyy"

    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

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

    fun isValidEmail(email: String): Boolean =
        !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun validateMinLength(password: String): Boolean =
        !TextUtils.isEmpty(password) && password.length >= Constants.MIN_LENGTH_PASSWORD

    fun toLatlng(lat: Double?, lng: Double?): LatLng? {
        println("test")
        return if (lat != null && lng != null) {
            LatLng(lat, lng)

        } else null
    }

    @Suppress("DEPRECATION")
    fun getStringAddress(
        latlng: LatLng?,
        context: Context
    ): String {
        var fullAddress = "-"

        try {
            if (latlng != null) {
                val address: Address?
                val gc = Geocoder(context, Locale.getDefault())
                val list: List<Address> =
                    gc.getFromLocation(latlng.latitude, latlng.longitude, 1) as List<Address>
                address = if (list.isNotEmpty()) list[0] else null

                if (address != null) {
                    val city = address.locality
                    val state = address.adminArea
                    val country = address.countryName

                    fullAddress = address.getAddressLine(0)
                        ?: if (city != null && state != null && country != null) {
                            StringBuilder(city).append(", $state").append(", $country")
                                .toString()
                        } else if (state != null && country != null) {
                            StringBuilder(state).append(", $country").toString()
                        } else country ?: "Location Name Unknown"
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR", "ERROR: $e")
        }
        return fullAddress
    }

}
