package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.media.AudioManager
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.viewbinding.ViewBinding
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener

@RequiresApi(Build.VERSION_CODES.S)
abstract class AbstractKeyboardView(
    context: Context, protected val keyboardModeChangeListener: KeyboardModeChangeListener
) {
    // abstract values; hard-code these values to use protected methods
    protected abstract val associatedKeyboardBinding: ViewBinding
    protected abstract val mode: KeyboardMode
    protected abstract val buttonStrings: Sequence<String>
    protected abstract val buttonSequence: Sequence<Button>

    // input connection and layout
    lateinit var inputConnection: InputConnection
    val root: LinearLayout by lazy { associatedKeyboardBinding.root as LinearLayout }

    // audio, vibration
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val vibratorManager =
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    private val vibrationDuration: Long = 50

    /* Kotlin warns of using non-final properties in init to keep its initialization order.
     * Make sure to use below functions to initialize. */
    protected fun initializeAllButtons() {
        val buttonIterator = buttonSequence.iterator()
        val stringIterator = buttonStrings.iterator()
        while (buttonIterator.hasNext()) {
            assert(stringIterator.hasNext())
            val button = buttonIterator.next()
            button.text = stringIterator.next()
            button.setOnClickListener(generateOnClickListener(button))
        }
    }

    // To avoid non-button components (e.g. spaces), we have to filter them by null.
    protected fun extractButtonFromKeyboardItem(v: View?): Button? = try {
        (v as ConstraintLayout).children.first() as Button
    } catch (e: ClassCastException) {
        null
    }

    private fun generateOnClickListener(keyButton: Button): OnClickListener {
        return OnClickListener {
            val keyText = keyButton.text
            audioManager.playSoundEffect(FunctionalKey.eval(keyText).getFX())
            vibratorManager.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        vibrationDuration, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            )
            val actualText = keyText.trim()
            when (FunctionalKey.eval(actualText)) {
                FunctionalKey.SHIFT -> clickShift()
                FunctionalKey.BACKSPACE -> clickBackspace()
                FunctionalKey.SPECIAL -> clickSpecial()
                FunctionalKey.LANGUAGE -> clickLanguage()
                FunctionalKey.SPACE -> clickSpace()
                FunctionalKey.ENTER -> clickEnter()
                FunctionalKey.OTHER -> clickOther()
                FunctionalKey.NONE -> clickGeneral(actualText)
            }
        }
    }

    protected abstract fun clickShift()

    protected open fun clickBackspace() {
        val t = SystemClock.uptimeMillis()
        inputConnection.sendKeyEvent(
            KeyEvent(
                t, t, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0
            )
        )
        inputConnection.sendKeyEvent(
            KeyEvent(
                t, t, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0
            )
        )
    }

    private fun clickSpecial() {
        KeyboardSpecial.returningMode = mode
        keyboardModeChangeListener.changeMode(KeyboardMode.SPECIAL)
    }

    protected abstract fun clickLanguage()

    protected open fun clickSpace() {
        inputConnection.commitText(" ", 1)
    }

    protected open fun clickEnter() {
        val t = SystemClock.uptimeMillis()
        inputConnection.sendKeyEvent(
            KeyEvent(
                t, t, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0
            )
        )
        inputConnection.sendKeyEvent(
            KeyEvent(
                t, t, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0
            )
        )
    }

    protected open fun clickOther() {}

    protected open fun clickGeneral(keyText: CharSequence) {
        assert(keyText.length == 1)
        inputConnection.commitText(keyText, 1)
    }
}