package com.example.submissionawalstoryapp.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.utils.Helper.validateMinLength
import com.google.android.material.textfield.TextInputEditText

class CustomEditPassword : TextInputEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                val isValid = validateMinLength(text.toString())
                error = if (isValid) null else context.getString(R.string.password_policy)
            }
        }
        addTextChangedListener(textWatcher)
    }
}
