package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.view.children
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener
import com.example.hangulkeyboard.databinding.KeyboardHangulBinding

class KeyboardKorean(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardModeChangeListener: KeyboardModeChangeListener
) : AbstractKeyboardView(context, keyboardModeChangeListener) {
    override val associatedKeyboardBinding = KeyboardHangulBinding.inflate(layoutInflater)

    override val buttonSequence: Sequence<Button> = sequenceOf(
        associatedKeyboardBinding.hangulNumpad,
        associatedKeyboardBinding.hangulFirstLine,
        associatedKeyboardBinding.hangulSecondLine,
        associatedKeyboardBinding.hangulThirdLine,
        associatedKeyboardBinding.hangulFourthLine
    ).flatMap { it.children }.mapNotNull { extractButtonFromKeyboardItem(it) }

    init {
        initializeAllButtons(
            sequenceOf(
                sequenceOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                sequenceOf("ㅣ", "ㆍ", "ㅡ", "←"),
                sequenceOf("ㄱ", "ㄴ", "ㄹ", "␣"),
                sequenceOf("ㅁ", "ㅅ", "ㅇ", "↑", "′"),
                sequenceOf("!@#", "⊕", ".", ",", "?", "!", "↵")
            ).flatten()
        )
    }

    private val composer = HangulComposer()

    // counts programmatic IC edits that will trigger onUpdateSelection, which should be ignored
    private var ignoreUpdateCount = 0

    private fun applyResult(result: ComposerResult) {
        when (result) {
            is ComposerResult.Composing -> ic.setComposingText(result.text, 1)
            is ComposerResult.Committed -> {
                ic.commitText(result.committed, 1)
                ic.setComposingText(result.composing, 1)
            }
            is ComposerResult.Finished -> {
                ic.finishComposingText()
                ic.setComposingText(result.composing, 1)
            }
            is ComposerResult.FlashFinish -> {
                ic.setComposingText(result.flash, 1)
                ignoreUpdateCount++
                ic.finishComposingText()
                ic.setComposingText(result.composing, 1)
            }
        }
    }

    override fun onInputConnectionSet() {
        ignoreUpdateCount++
        composer.reset()
        ic.setComposingText("", 1)
    }

    override fun onUpdateSelection() {
        if (ignoreUpdateCount > 0) ignoreUpdateCount--
        else {
            ic.finishComposingText()
            composer.finish()
        }
    }

    override fun clickShift() = applyResult(composer.applyDouble())

    override fun clickBackspace() {
        if (composer.erase()) {
            ic.setComposingText(composer.composingText, 1)
        } else {
            super.clickBackspace()
            ic.setComposingText("", 1)
        }
    }

    override fun clickSpecial() {
        ic.finishComposingText()
        composer.finish()
        super.clickSpecial()
    }

    override fun clickLanguage() {
        ic.finishComposingText()
        composer.finish()
        keyboardModeChangeListener.changeMode(KeyboardMode.ENGLISH)
    }

    override fun clickSpace() {
        ignoreUpdateCount++
        if (composer.composingText.isEmpty()) {
            super.clickSpace()
        } else {
            ic.finishComposingText()
            composer.finish()
        }
    }

    override fun clickOther() = applyResult(composer.applyStroke())

    override fun clickGeneral(keyText: CharSequence) {
        require(keyText.length == 1) { "keyText must be a single character" }
        check(ic.beginBatchEdit()) { "beginBatchEdit failed" }
        applyResult(composer.addPhoneme(keyText[0]))
        ignoreUpdateCount++
        ic.endBatchEdit()
    }
}
