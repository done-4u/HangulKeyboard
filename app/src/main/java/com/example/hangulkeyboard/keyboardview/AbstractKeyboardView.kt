package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
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
    // layout
    protected abstract val associatedKeyboardBinding: ViewBinding
    val root: LinearLayout by lazy { associatedKeyboardBinding.root as LinearLayout }
    var inputConnection: InputConnection? = null

    // audio, vibration
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val vibratorManager =
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    private val vibrationDuration: Long = 50

    // hard-code this values to use protected methods
    protected abstract val buttonStrings: List<String>
    protected abstract val buttonSequence: Sequence<Button>

    /* Since Kotlin warns of using non-final properties in init, it cannot be inherited.
     * Instead, use below functions to initialize. */
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

    /* Inserting boiler-plate, repeated codes to bind views by View Binding is messy.
     * Use this function and loop to make code readable. */
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

    protected open fun clickShift() {}

    protected open fun clickBackspace() {
        inputConnection?.deleteSurroundingTextInCodePoints(1, 0)
    }

    protected open fun clickSpecial() {
        keyboardModeChangeListener.changeMode(KeyboardMode.SPECIAL)
    }

    protected open fun clickLanguage() {
        keyboardModeChangeListener.changeMode(KeyboardMode.ENGLISH)
    }

    protected open fun clickSpace() {
        inputConnection?.commitText(" ", 1)
    }

    protected open fun clickEnter() {
        inputConnection?.commitText("\n", 1)
    }

    protected open fun clickOther() {}

    protected open fun clickGeneral(keyText: CharSequence) {
        assert(keyText.length == 1)
        inputConnection?.commitText(keyText, 1)
    }
}