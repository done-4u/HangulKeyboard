package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener
import com.example.hangulkeyboard.databinding.KeyboardSymbolBinding

@RequiresApi(Build.VERSION_CODES.S)
class KeyboardSpecial(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardModeChangeListener: KeyboardModeChangeListener
) : AbstractKeyboardView(context, keyboardModeChangeListener) {
    override val associatedKeyboardBinding = KeyboardSymbolBinding.inflate(layoutInflater)
    override val mode = KeyboardMode.SPECIAL

    override val buttonStrings = sequenceOf(
        sequenceOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        sequenceOf(".", ",", "?", " ! ", ":"),
        sequenceOf("-", "_", "'", "\"", ";"),
        sequenceOf("↑", "@", "#", "%", "←"),
        sequenceOf("⊕", "(", "␣", ")", "↵")
    ).flatten()

    private val buttonShiftStrings = sequenceOf(
        sequenceOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        sequenceOf("<", ">", "`", "&", "^"),
        sequenceOf("+", "*", "/", "~", "&"),
        sequenceOf("↑", "{", "=", "}", "←"),
        sequenceOf("⊕", "[", "␣", "]", "↵")
    ).flatten()

    override val buttonSequence: Sequence<Button> = sequenceOf(
        associatedKeyboardBinding.symbolNumpad,
        associatedKeyboardBinding.symbolFirstLine,
        associatedKeyboardBinding.symbolSecondLine,
        associatedKeyboardBinding.symbolThirdLine,
        associatedKeyboardBinding.symbolFourthLine
    ).map { it.children }.flatten().map { extractButtonFromKeyboardItem(it) }.filterNotNull()

    init {
        initializeAllButtons()
    }

    private var isOnShift = false
        set(value) {
            val buttonIterator = buttonSequence.iterator()
            val stringIterator =
                if (value) buttonShiftStrings.iterator() else buttonStrings.iterator()
            while (buttonIterator.hasNext()) {
                assert(stringIterator.hasNext())
                buttonIterator.next().text = stringIterator.next()
            }
            field = value
        }

    override fun clickShift() {
        isOnShift = !isOnShift
    }

    override fun clickLanguage() {
        keyboardModeChangeListener.changeMode(returningMode)
    }

    companion object {
        var returningMode = KeyboardMode.ENGLISH
    }
}
