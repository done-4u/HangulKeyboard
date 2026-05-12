package com.example.hangulkeyboard

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.hangulkeyboard.keyboardview.AbstractKeyboardView
import com.example.hangulkeyboard.keyboardview.KeyboardEnglish
import com.example.hangulkeyboard.keyboardview.KeyboardKorean
import com.example.hangulkeyboard.keyboardview.KeyboardSpecial

class KeyboardService : InputMethodService() {
    private var currentMode = KeyboardMode.ENGLISH

    private val keyboardModeChangeListener = KeyboardModeChangeListener { mode ->
        if (mode == KeyboardMode.SPECIAL) {
            (modeKeyboardViewMap[KeyboardMode.SPECIAL] as KeyboardSpecial).returningMode = currentMode
        }
        currentMode = mode
        currentInputConnection?.finishComposingText()
        val keyboard = modeKeyboardViewMap[mode] ?: return@KeyboardModeChangeListener
        keyboard.inputConnection = currentInputConnection
        setInputView(keyboard.root)
    }

    // Deferred to avoid NullPointerException from applicationContext; reset on configuration
    // change so views are re-inflated with the correct orientation-specific layout.
    private var _modeKeyboardViewMap: Map<KeyboardMode, AbstractKeyboardView>? = null
    private val modeKeyboardViewMap: Map<KeyboardMode, AbstractKeyboardView>
        get() = _modeKeyboardViewMap ?: mapOf(
            KeyboardMode.KOREAN to KeyboardKorean(
                applicationContext, layoutInflater, keyboardModeChangeListener
            ), KeyboardMode.ENGLISH to KeyboardEnglish(
                applicationContext, layoutInflater, keyboardModeChangeListener
            ), KeyboardMode.SPECIAL to KeyboardSpecial(
                applicationContext, layoutInflater, keyboardModeChangeListener
            )
        ).also { _modeKeyboardViewMap = it }

    private fun getCurrentView(): AbstractKeyboardView {
        return modeKeyboardViewMap[currentMode] ?: error("No keyboard for mode $currentMode")
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        keyboardModeChangeListener.changeMode(currentMode)
    }

    override fun updateInputViewShown() {
        super.updateInputViewShown()
        keyboardModeChangeListener.changeMode(currentMode)
    }

    override fun onCreateInputView(): View {
        val currentView = getCurrentView()
        currentView.inputConnection = currentInputConnection
        return currentView.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        _modeKeyboardViewMap = null
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
