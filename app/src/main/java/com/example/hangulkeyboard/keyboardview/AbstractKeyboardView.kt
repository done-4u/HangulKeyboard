package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.media.AudioManager
import android.os.CombinedVibration
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.viewbinding.ViewBinding
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener

abstract class AbstractKeyboardView(
    context: Context, protected val keyboardModeChangeListener: KeyboardModeChangeListener
) {
    // abstract values; hard-code these values to use protected methods
    protected abstract val associatedKeyboardBinding: ViewBinding
    protected abstract val buttonSequence: Sequence<Button>

    // input connection and layout
    protected open fun onInputConnectionSet() {}
    var inputConnection: InputConnection? = null
        set(value) {
            field = value
            onInputConnectionSet()
        }
    protected val ic: InputConnection
        get() = inputConnection ?: error("InputConnection is not set")
    val root: View get() = associatedKeyboardBinding.root

    // audio, vibration
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val vibratorManager =
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    private val vibrationDuration: Long = 50

    /* Kotlin warns of using non-final properties in init to keep its initialization order.
     * Make sure to use below functions to initialize. */
    protected fun initializeAllButtons(buttonStrings: Sequence<String>) {
        val buttonIterator = buttonSequence.iterator()
        val stringIterator = buttonStrings.iterator()
        while (buttonIterator.hasNext()) {
            check(stringIterator.hasNext()) { "buttonStrings has fewer items than buttonSequence" }
            val button = buttonIterator.next()
            val str = stringIterator.next()
            button.text = str
            button.tag = FunctionalKey.eval(str)
            button.setOnClickListener(generateOnClickListener(button))
        }
    }

    protected fun extractButtonFromKeyboardItem(v: View?): Button? =
        ((v as? ConstraintLayout)?.children?.firstOrNull()) as? Button

    private fun generateOnClickListener(keyButton: Button): OnClickListener {
        return OnClickListener {
            val functionalKey = keyButton.tag as FunctionalKey
            audioManager.playSoundEffect(functionalKey.getFX())
            vibratorManager.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        vibrationDuration, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            )
            when (functionalKey) {
                FunctionalKey.SHIFT -> clickShift()
                FunctionalKey.BACKSPACE -> clickBackspace()
                FunctionalKey.SPECIAL -> clickSpecial()
                FunctionalKey.LANGUAGE -> clickLanguage()
                FunctionalKey.SPACE -> clickSpace()
                FunctionalKey.ENTER -> clickEnter()
                FunctionalKey.OTHER -> clickOther()
                FunctionalKey.NONE -> clickGeneral(keyButton.text.toString())
            }
        }
    }

    open fun onUpdateSelection() {}

    protected abstract fun clickShift()

    protected open fun clickBackspace() {
        val t = SystemClock.uptimeMillis()
        ic.sendKeyEvent(KeyEvent(t, t, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0))
        ic.sendKeyEvent(KeyEvent(t, t, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0))
    }

    protected open fun clickSpecial() {
        keyboardModeChangeListener.changeMode(KeyboardMode.SPECIAL)
    }

    protected abstract fun clickLanguage()

    protected open fun clickSpace() {
        ic.commitText(" ", 1)
    }

    protected open fun clickEnter() {
        val t = SystemClock.uptimeMillis()
        ic.sendKeyEvent(KeyEvent(t, t, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0))
        ic.sendKeyEvent(KeyEvent(t, t, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0))
    }

    protected open fun clickOther() {}

    protected open fun clickGeneral(keyText: CharSequence) {
        require(keyText.length == 1) { "keyText must be a single character" }
        ic.commitText(keyText, 1)
    }
}
