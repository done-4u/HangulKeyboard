package com.example.hangulkeyboard

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.hangulkeyboard.keyboardview.AbstractKeyboardView
import com.example.hangulkeyboard.keyboardview.KeyboardEnglish
import com.example.hangulkeyboard.keyboardview.KeyboardKorean
import com.example.hangulkeyboard.keyboardview.KeyboardSpecial

@RequiresApi(Build.VERSION_CODES.S)
class KeyboardService : InputMethodService() {
    var currentMode = KeyboardMode.ENGLISH

    private val keyboardModeChangeListener = object : KeyboardModeChangeListener {
        override fun changeMode(mode: KeyboardMode) {
            currentMode = mode
            currentInputConnection.finishComposingText()
            modeKeyboardViewMap[mode]!!.inputConnection = currentInputConnection
            setInputView(modeKeyboardViewMap[mode]!!.root)
        }
    }

    // lazy loading is needed to prevent NullPointerException from applicationContext
    val modeKeyboardViewMap: Map<KeyboardMode, AbstractKeyboardView> by lazy {
        mapOf(
            KeyboardMode.KOREAN to KeyboardKorean(
                applicationContext, layoutInflater, keyboardModeChangeListener
            ), KeyboardMode.ENGLISH to KeyboardEnglish(
                applicationContext, layoutInflater, keyboardModeChangeListener
            ), KeyboardMode.SPECIAL to KeyboardSpecial(
                applicationContext, layoutInflater, keyboardModeChangeListener
            )
        )
    }

    private fun getCurrentView(): AbstractKeyboardView {
        return modeKeyboardViewMap[currentMode]!!
    }

    override fun updateInputViewShown() {
        super.updateInputViewShown()
        keyboardModeChangeListener.changeMode(currentMode)
    }

    override fun onCreateInputView(): View {
        val foo = getCurrentView()
        foo.inputConnection = currentInputConnection
        return foo.root
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd
        )
        getCurrentView().onUpdateSelection()
    }
}
