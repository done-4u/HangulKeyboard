package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener
import com.example.hangulkeyboard.databinding.KeyboardHangulBinding

@RequiresApi(Build.VERSION_CODES.S)
class KeyboardKorean(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardModeChangeListener: KeyboardModeChangeListener
) : AbstractKeyboardView(context, keyboardModeChangeListener) {
    override val associatedKeyboardBinding = KeyboardHangulBinding.inflate(layoutInflater)
    override val mode = KeyboardMode.KOREAN

    override val buttonStrings = sequenceOf(
        sequenceOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        sequenceOf("ㅣ", "ㆍ", "ㅡ", "←"),
        sequenceOf("ㄱ", "ㄴ", "ㄹ", "␣"),
        sequenceOf("ㅁ", "ㅅ", "ㅇ", "↑", "ㅿ"),
        sequenceOf("!@#", "⊕", ".", ",", "?", " ! ", "↵")
    ).flatten()

    override val buttonSequence: Sequence<Button> = sequenceOf(
        associatedKeyboardBinding.hangulNumpad,
        associatedKeyboardBinding.hangulFirstLine,
        associatedKeyboardBinding.hangulSecondLine,
        associatedKeyboardBinding.hangulThirdLine,
        associatedKeyboardBinding.hangulFourthLine
    ).map { it.children }.flatten().map { extractButtonFromKeyboardItem(it) }.filterNotNull()

    init {
        initializeAllButtons()
    }

    // information of composing text
    // since their internal attributes change, not themselves, reflex by setter is not helpful
    private enum class ComposingState { NONE, SOLE, BOTH }

    private var composingState = ComposingState.NONE
    private var prevHangulChar: HangulChar? = null
    private var currHangulChar = HangulChar()

    // DO NOT forget to call this whenever you affect composing characters!!
    private fun reflex() {
        composingState = if (prevHangulChar == null) {
            if (currHangulChar.isNull()) {
                ComposingState.NONE
            } else {
                ComposingState.SOLE
            }
        } else {
            assert(!prevHangulChar!!.isNull())
            if (currHangulChar.isNull()) {
                Log.d("composition reflex", "CAUTION! PULLED CURR")
                currHangulChar = prevHangulChar!!
                prevHangulChar = null
                ComposingState.SOLE
            } else {
                ComposingState.BOTH
            }
        }
    }

    // to ignore OnUpdate (for proper reset of composingState)
    private var ignoreOnUpdateOnce = true
        set(value) {
            Log.d("update selection", "set-ignore: $value")
            field = value
        }

    private fun compose() {
        val text = when (composingState) {
            ComposingState.NONE -> ""
            ComposingState.SOLE -> currHangulChar.toString()
            ComposingState.BOTH -> prevHangulChar!!.toChar()!! + currHangulChar.toString()
        }
        inputConnection!!.setComposingText(text, 1)
    }

    private fun reflexAndCompose() {
        reflex()
        compose()
    }

    private fun resetComposition() {
        prevHangulChar = null
        currHangulChar = HangulChar()
        reflexAndCompose()
    }

    // finishComposingText MUST be called through this method
    private fun finishComposition() {
        inputConnection!!.finishComposingText()
        prevHangulChar = null
        currHangulChar = HangulChar()
        reflex()
    }

    private fun commitPrev() {
        inputConnection!!.commitText(prevHangulChar!!.toString(), 1)
        prevHangulChar = null
        reflex()
    }

    override fun onInputConnectionSet() {
        ignoreOnUpdateOnce = true
        resetComposition()
    }

    override fun onUpdateSelection() {
        Log.d("update selection", "onUpdateSelection()")
        if (ignoreOnUpdateOnce) {
            Log.d("update selection", "ignored!")
            ignoreOnUpdateOnce = false
        } else {
            Log.d("update selection", "finish composition")
            finishComposition()
        }
    }

    override fun clickShift() {
        currHangulChar.double()
        reflexAndCompose()
    }

    override fun clickBackspace() {
        when (composingState) {
            ComposingState.NONE -> super.clickBackspace()
            ComposingState.SOLE, ComposingState.BOTH -> currHangulChar.erase()
        }

        reflexAndCompose()
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
        ignoreOnUpdateOnce = true
        if (composingState == ComposingState.NONE) {
            super.clickSpace()
        } else {
            finishComposition()
        }
    }

    override fun clickOther() {
        currHangulChar.stroke()
        reflexAndCompose()
    }

    override fun clickGeneral(keyText: CharSequence) {
        assert(keyText.length == 1)
        val phoneme = keyText[0]

        assert(inputConnection!!.beginBatchEdit())

        val addOverflow = currHangulChar.addAtomicPhoneme(phoneme)
        reflex()
        assert(composingState != ComposingState.NONE)

        // below control-flow can be shortened, but intentionally did not for easy understanding
        if (addOverflow == null) {
            if (composingState == ComposingState.SOLE) {
                // "" + ㅅ -> ㅅ
                // 갑 + ㅅ -> 값
                compose()
            } else {
                assert(composingState == ComposingState.BOTH)
                // 난ㅇ + ㅣ -> 난이
                val phonemeCurr = currHangulChar.toPhoneme()
                if (phonemeCurr != null) {
                    if (currHangulChar.absorbFinalConsonant(prevHangulChar!!)) {
                        // 괄ㆍ + ㅡ -> 과로
                        // 맔ㆍ + ㅡ -> 말소
                        commitPrev()
                        compose()
                    } else {
                        // ㅁㆍ + ㅣ -> 머
                        assert(prevHangulChar!!.vowelPermeate(phoneme))
                    }
                }
            }
        } else if (addOverflow.connected) {
            if (composingState == ComposingState.SOLE) {
                // 난 + ㅇ -> 난ㅇ
                prevHangulChar = addOverflow.prev
                reflexAndCompose()
            } else {
                assert(composingState == ComposingState.BOTH)
                // 언ㅅ + ㆍ  -> 언/ㅅㆍ
                commitPrev()
                prevHangulChar = addOverflow.prev
                reflexAndCompose()
            }
        } else {
            // 각 + ㅁ -> 각/ㅁ
            // 앙 + ㅣ -> 아/이
            // 난ㅇ + ㄱ -> 난ㅇㄱ
            val foo = currHangulChar
            currHangulChar = addOverflow.prev
            finishComposition()
            currHangulChar = foo
            reflexAndCompose()
        }

        ignoreOnUpdateOnce = true
        inputConnection!!.endBatchEdit()
    }
}