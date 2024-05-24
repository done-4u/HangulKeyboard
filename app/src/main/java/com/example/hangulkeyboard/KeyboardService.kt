package com.example.hangulkeyboard

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.hangulkeyboard.keyboardview.*

@RequiresApi(Build.VERSION_CODES.S)
class KeyboardService : InputMethodService() {
    private val keyboardModeChangeListener = object : KeyboardModeChangeListener {
        override fun changeMode(mode: KeyboardMode) {
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

    override fun onCreateInputView(): View = modeKeyboardViewMap[KeyboardMode.ENGLISH]!!.root
}