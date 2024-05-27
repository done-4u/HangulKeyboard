package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.example.hangulkeyboard.KeyboardModeChangeListener
import com.example.hangulkeyboard.databinding.KeyboardSymbolBinding

@RequiresApi(Build.VERSION_CODES.S)
class KeyboardSpecial(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardModeChangeListener: KeyboardModeChangeListener
) : AbstractKeyboardView(context, keyboardModeChangeListener) {
    override val associatedKeyboardBinding = KeyboardSymbolBinding.inflate(layoutInflater)

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
        sequenceOf(" + ", "*", "/", "~", "&"),
        sequenceOf("↑", "{", "=", "}", "←"),
        sequenceOf("⊕", "[", "␣", "]", "↵")
    ).flatten()

    override val buttonSequence: Sequence<Button> = sequenceOf(
        associatedKeyboardBinding.symbolNumpad,
        associatedKeyboardBinding.symbolFirstLine,
        associatedKeyboardBinding.symbolSecondLine,
        associatedKeyboardBinding.symbolThirdLine,
        associatedKeyboardBinding.symbolFourthLine
    ).map { line: LinearLayout -> line.children }.flatten()
        .map { v: View -> extractButtonFromKeyboardItem(v) }.filterNotNull()

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
}