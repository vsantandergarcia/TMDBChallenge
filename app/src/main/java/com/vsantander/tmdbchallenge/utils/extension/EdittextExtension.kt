package com.vsantander.tmdbchallenge.utils.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.textWatcherOnArfterTextChanged(afterTextChanged: (s: Editable?) -> Unit = {}) {
    this.textWatcher(afterTextChanged = afterTextChanged)
}

fun EditText.textWatcher(
        afterTextChanged: (s: Editable?) -> Unit = {},
        beforeTextChanged: (s: CharSequence?) -> Unit = {},
        onTextChanged: (s: CharSequence?) -> Unit = {}
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s)
        }

    })
}