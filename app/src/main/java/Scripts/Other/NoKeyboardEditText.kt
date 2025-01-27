package Scripts.Other

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.view.MotionEvent
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText

class NoKeyboardEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr)  {

    init {
        // Отключаем автоматическое появление клавиатуры при фокусе
        this.showSoftInputOnFocus = false
    }

    override fun onCheckIsTextEditor(): Boolean {
        // Сообщаем системе, что это текстовый редактор (чтобы курсор работал)
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Включаем видимость курсора и выделение текста при касании
        this.isCursorVisible = true
        return super.onTouchEvent(event)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            // Прячем клавиатуру при фокусе
            val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}