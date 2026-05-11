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
                sequenceOf("ㅁ", "ㅅ", "ㅇ", "↑", "ㅿ"),
                sequenceOf("!@#", "⊕", ".", ",", "?", "!", "↵")
            ).flatten()
        )
    }

    // information of composing text
    // since their internal attributes change, not themselves, syncState by setter is not helpful
    private enum class ComposingState { NONE, SOLE, BOTH }

    private var composingState = ComposingState.NONE
    private var prevHangulChar: HangulChar? = null
    private var currHangulChar = HangulChar()

    // DO NOT forget to call this whenever you affect composing characters!!
    private fun syncState() {
        composingState = if (prevHangulChar == null) {
            if (currHangulChar.isEmpty()) {
                ComposingState.NONE
            } else {
                ComposingState.SOLE
            }
        } else {
            val prev = checkNotNull(prevHangulChar)
            check(!prev.isEmpty()) { "prevHangulChar must not be in empty state" }
            if (currHangulChar.isEmpty()) {
                currHangulChar = prev
                prevHangulChar = null
                ComposingState.SOLE
            } else {
                ComposingState.BOTH
            }
        }
    }

    // counts programmatic IC edits that will trigger onUpdateSelection, which should be ignored
    private var ignoreUpdateCount = 0

    private fun compose() {
        val text = when (composingState) {
            ComposingState.NONE -> ""
            ComposingState.SOLE -> currHangulChar.toString()
            ComposingState.BOTH -> checkNotNull(prevHangulChar).toString() + currHangulChar.toString()
        }
        ic.setComposingText(text, 1)
    }

    private fun syncStateAndCompose() {
        syncState()
        compose()
    }

    private fun resetComposition() {
        prevHangulChar = null
        currHangulChar = HangulChar()
        syncStateAndCompose()
    }

    // finishComposingText MUST be called through this method
    private fun finishComposition() {
        ic.finishComposingText()
        prevHangulChar = null
        currHangulChar = HangulChar()
        syncState()
    }

    private fun commitPrev() {
        ic.commitText(checkNotNull(prevHangulChar).toString(), 1)
        prevHangulChar = null
        syncState()
    }

    override fun onInputConnectionSet() {
        ignoreUpdateCount++
        resetComposition()
    }

    override fun onUpdateSelection() {
        if (ignoreUpdateCount > 0) ignoreUpdateCount--
        else finishComposition()
    }

    private fun applyTransform(transform: HangulChar.() -> OverflowResult?) {
        val overflow = currHangulChar.transform()
        if (overflow != null) {
            val incoming = currHangulChar
            currHangulChar = overflow.prev
            compose()
            ignoreUpdateCount++
            finishComposition()
            currHangulChar = incoming
        }
        syncStateAndCompose()
    }

    override fun clickShift() = applyTransform(HangulChar::double)

    override fun clickBackspace() {
        when (composingState) {
            ComposingState.NONE -> super.clickBackspace()
            ComposingState.SOLE, ComposingState.BOTH -> currHangulChar.erase()
        }

        syncStateAndCompose()
    }

    override fun clickSpecial() {
        finishComposition()
        super.clickSpecial()
    }

    override fun clickLanguage() {
        finishComposition()
        keyboardModeChangeListener.changeMode(KeyboardMode.ENGLISH)
    }

    override fun clickSpace() {
        ignoreUpdateCount++
        if (composingState == ComposingState.NONE) {
            super.clickSpace()
        } else {
            finishComposition()
        }
    }

    override fun clickOther() = applyTransform(HangulChar::stroke)

    override fun clickGeneral(keyText: CharSequence) {
        require(keyText.length == 1) { "keyText must be a single character" }
        val phoneme = keyText[0]

        check(ic.beginBatchEdit()) { "beginBatchEdit failed" }

        val addOverflow = currHangulChar.addAtomicPhoneme(phoneme)
        syncState()
        check(composingState != ComposingState.NONE) { "Expected composing state after addAtomicPhoneme" }

        // below control-flow can be shortened, but intentionally did not for easy understanding
        if (addOverflow == null) {
            if (composingState == ComposingState.SOLE) {
                // "" + ㅅ -> ㅅ
                // 갑 + ㅅ -> 값
                compose()
            } else {
                check(composingState == ComposingState.BOTH) { "Expected BOTH state" }
                // 난ㅇ + ㅣ -> 난이
                val prev = checkNotNull(prevHangulChar)
                val phonemeCurr = currHangulChar.toPhoneme()
                if (phonemeCurr != null) {
                    if (currHangulChar.absorbFinalConsonant(prev)) {
                        // 괄ㆍ + ㅡ -> 과로
                        // 맔ㆍ + ㅡ -> 말소
                        commitPrev()
                        compose()
                    } else {
                        // ㅁㆍ + ㅣ -> 머
                        check(prev.vowelPermeate(phoneme)) { "vowelPermeate failed unexpectedly" }
                    }
                }
            }
        } else if (addOverflow.connected) {
            if (composingState == ComposingState.SOLE) {
                // 난 + ㅇ -> 난ㅇ
                prevHangulChar = addOverflow.prev
                syncStateAndCompose()
            } else {
                check(composingState == ComposingState.BOTH) { "Expected BOTH state" }
                // 언ㅅ + ㆍ  -> 언/ㅅㆍ
                commitPrev()
                prevHangulChar = addOverflow.prev
                syncStateAndCompose()
            }
        } else {
            // 각 + ㅁ -> 각/ㅁ
            // 앙 + ㅣ -> 아/이
            // 난ㅇ + ㄱ -> 난ㅇㄱ
            val incoming = currHangulChar
            currHangulChar = addOverflow.prev
            finishComposition()
            currHangulChar = incoming
            syncStateAndCompose()
        }

        ignoreUpdateCount++
        ic.endBatchEdit()
    }
}
