package com.example.prototipoitv

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import java.util.Locale

// Some soft keyboards send deleteSurroundingText rather than a backspace key.
private class LetterField(context: Context, val previous: () -> Unit) : EditText(context) {
    private fun moveBack(): Boolean {
        if (!text.isNullOrEmpty()) return false
        previous()
        return true
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
        (keyCode == KeyEvent.KEYCODE_DEL && moveBack()) || super.onKeyDown(keyCode, event)

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val base = super.onCreateInputConnection(outAttrs) ?: return null
        return object : InputConnectionWrapper(base, false) {
            override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean =
                (beforeLength > 0 && moveBack()) || super.deleteSurroundingText(beforeLength, afterLength)
            override fun deleteSurroundingTextInCodePoints(beforeLength: Int, afterLength: Int): Boolean =
                (beforeLength > 0 && moveBack()) || super.deleteSurroundingTextInCodePoints(beforeLength, afterLength)
        }
    }
}

@Composable
internal fun PlateFields(numbers: String, letters: String, setNumbers: (String) -> Unit, setLetters: (String) -> Unit) {
    Text("Matrícula")
    AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
        val row = LinearLayout(context)
        val digits = EditText(context)
        val ime = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        fun focus(field: EditText) {
            field.requestFocus()
            field.setSelection(field.length())
            ime.showSoftInput(field, 0)
        }
        val suffix = LetterField(context) { focus(digits) }
        fun add(field: EditText, hintText: String, description: String, input: Int, allowed: String) {
            field.setSingleLine()
            field.inputType = input
            field.hint = hintText
            field.contentDescription = description
            field.filters = arrayOf(InputFilter { source, start, end, _, _, _ ->
                val entered = source.subSequence(start, end).toString()
                val filtered = entered.uppercase(Locale.ROOT).filter { it in allowed }
                if (entered == filtered) null else filtered
            }, InputFilter.LengthFilter(hintText.length))
            row.addView(field, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
        add(digits, "0000", "Cuatro cifras de matrícula", InputType.TYPE_CLASS_NUMBER, "0123456789")
        add(suffix, "BCD", "Tres consonantes de matrícula", InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "BCDFGHJKLMNPRSTVWXYZ")
        digits.imeOptions = EditorInfo.IME_ACTION_NEXT
        suffix.imeOptions = EditorInfo.IME_ACTION_DONE
        digits.setText(numbers)
        suffix.setText(letters)
        digits.doAfterTextChanged {
            setNumbers(it.toString())
            if (it?.length == 4 && digits.hasFocus()) focus(suffix)
        }
        suffix.doAfterTextChanged { setLetters(it.toString()) }
        row
    })
}
