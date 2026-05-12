package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.view.children
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener
import com.example.hangulkeyboard.databinding.KeyboardSymbolBinding

class KeyboardSpecial(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardModeChangeListener: KeyboardModeChangeListener
) : AbstractKeyboardView(context, keyboardModeChangeListener) {
    override val associatedKeyboardBinding = KeyboardSymbolBinding.inflate(layoutInflater)

    private val buttonStrings = sequenceOf(
        sequenceOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        sequenceOf(".", ",", "?", "!", ":"),
        sequenceOf("-", "_", "'", "\"", ";"),
        sequenceOf(FunctionalKey.LABEL_SHIFT, "@", "#", "%", FunctionalKey.LABEL_BACKSPACE),
        sequenceOf(FunctionalKey.LABEL_LANGUAGE, "(", FunctionalKey.LABEL_SPACE, ")", FunctionalKey.LABEL_ENTER)
    ).flatten()

    private val buttonShiftStrings = sequenceOf(
        sequenceOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        sequenceOf("<", ">", "`", "&", "^"),
        sequenceOf("+", "*", "/", "~", "\\"),
        sequenceOf(FunctionalKey.LABEL_SHIFT, "{", "=", "}", FunctionalKey.LABEL_BACKSPACE),
        sequenceOf(FunctionalKey.LABEL_LANGUAGE, "[", FunctionalKey.LABEL_SPACE, "]", FunctionalKey.LABEL_ENTER)
    ).flatten()

    override val buttonSequence: Sequence<Button> = sequenceOf(
        associatedKeyboardBinding.symbolNumpad,
        associatedKeyboardBinding.symbolFirstLine,
        associatedKeyboardBinding.symbolSecondLine,
        associatedKeyboardBinding.symbolThirdLine,
        associatedKeyboardBinding.symbolFourthLine
    ).flatMap { it.children }.mapNotNull { extractButtonFromKeyboardItem(it) }

    init {
        initializeAllButtons(buttonStrings)
    }

    private var isOnShift = false
        set(value) {
            val stringIterator =
                if (value) buttonShiftStrings.iterator() else buttonStrings.iterator()
            for (button in buttonSequence) {
                button.text = stringIterator.next()
            }
            field = value
        }

    override fun clickShift() {
        isOnShift = !isOnShift
    }

    private var returningMode = KeyboardMode.ENGLISH

    fun enterFrom(mode: KeyboardMode) {
        returningMode = mode
    }

    override fun clickLanguage() {
        keyboardModeChangeListener.changeMode(returningMode)
    }
}
