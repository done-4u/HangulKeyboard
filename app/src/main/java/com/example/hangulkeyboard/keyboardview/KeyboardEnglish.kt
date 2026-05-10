package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.view.children
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener
import com.example.hangulkeyboard.databinding.KeyboardAlphabetBinding

class KeyboardEnglish(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardModeChangeListener: KeyboardModeChangeListener
) : AbstractKeyboardView(context, keyboardModeChangeListener) {
    override val associatedKeyboardBinding = KeyboardAlphabetBinding.inflate(layoutInflater)

    override val buttonSequence: Sequence<Button> = sequenceOf(
        associatedKeyboardBinding.alphabetNumpad,
        associatedKeyboardBinding.alphabetFirstLine,
        associatedKeyboardBinding.alphabetSecondLine,
        associatedKeyboardBinding.alphabetThirdLine,
        associatedKeyboardBinding.alphabetFourthLine
    ).map { it.children }.flatten().map { extractButtonFromKeyboardItem(it) }.filterNotNull()

    init {
        initializeAllButtons(
            sequenceOf(
                sequenceOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                sequenceOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                sequenceOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                sequenceOf("↑", "z", "x", "c", "v", "b", "n", "m", "←"),
                sequenceOf("!@#", "⊕", ",", "␣", ".", "↵")
            ).flatten()
        )
    }

    private enum class CapsMode {
        LOWER, UPPER_ONCE, UPPER_FIXED;

        fun next() = when (this) {
            LOWER -> UPPER_ONCE
            UPPER_ONCE -> UPPER_FIXED
            UPPER_FIXED -> LOWER
        }
    }

    private var capsMode = CapsMode.LOWER
        set(value) {
            for (button in buttonSequence) {
                button.text = when (button.tag as FunctionalKey) {
                    FunctionalKey.SHIFT -> when (value) {
                        CapsMode.LOWER -> "↑"
                        CapsMode.UPPER_ONCE -> "↑↑"
                        CapsMode.UPPER_FIXED -> "↑↑↑"
                    }
                    FunctionalKey.NONE -> when (value) {
                        CapsMode.LOWER -> button.text.toString().lowercase()
                        else -> button.text.toString().uppercase()
                    }
                    else -> button.text
                }
            }
            field = value
        }

    override fun clickShift() {
        capsMode = capsMode.next()
    }

    override fun clickLanguage() {
        keyboardModeChangeListener.changeMode(KeyboardMode.KOREAN)
    }

    override fun clickGeneral(keyText: CharSequence) {
        super.clickGeneral(keyText)
        if (capsMode == CapsMode.UPPER_ONCE) {
            capsMode = CapsMode.LOWER
        }
    }
}
