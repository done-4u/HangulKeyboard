package com.example.hangulkeyboard.keyboardview

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.example.hangulkeyboard.KeyboardMode
import com.example.hangulkeyboard.KeyboardModeChangeListener
import com.example.hangulkeyboard.databinding.KeyboardHangulBinding

@RequiresApi(Build.VERSION_CODES.S)
class KeyboardKorean constructor(
    context: Context,
    layoutInflater: LayoutInflater,
    keyboardModeChangeListener: KeyboardModeChangeListener
) : AbstractKeyboardView(context, keyboardModeChangeListener) {

    private class HangulChar {
        private enum class State { NONE, SOLE_CONSONANT, SOLE_VOWEL, NO_FINAL, FULL }

        var state: State = State.NONE
        var initialConsonant: Char? = null
        var medialVowel: Char? = null
        var finalConsonant: Char? = null

        val doubleFinalConsonantMap = mapOf(
            'ㄱ' to mapOf('ㅅ' to 'ㄳ'), 'ㄴ' to mapOf('ㅈ' to 'ㄵ', 'ㅎ' to 'ㄶ'), 'ㄹ' to mapOf(
                'ㄱ' to 'ㄺ', 'ㅁ' to 'ㄻ', 'ㅂ' to 'ㄼ', 'ㅅ' to 'ㄽ', 'ㅌ' to 'ㄾ', 'ㅍ' to 'ㄿ', 'ㅎ' to 'ㅀ'
            ), 'ㅂ' to mapOf('ㅅ' to 'ㅄ')
        )

        fun reflex() {
            // updating state
            state = if (finalConsonant == null) {
                if (initialConsonant == null) {
                    if (medialVowel == null) State.NONE
                    else State.SOLE_VOWEL
                } else {
                    if (medialVowel == null) State.SOLE_CONSONANT
                    else State.NO_FINAL
                }
            } else {
                if (initialConsonant == null || medialVowel == null) throw IllegalStateException()
                else State.FULL
            }

            // assembling phonemes
            val assembled: Char? = when (state) {
                State.NONE -> null
                State.SOLE_CONSONANT -> initialConsonant
                State.SOLE_VOWEL -> medialVowel
                else -> TODO("Not implemented yet")
            }
        }

        fun addConsonant(consonant: Char) {
            when (state) {
                State.NONE -> initialConsonant = consonant
                State.SOLE_CONSONANT, State.SOLE_VOWEL -> {
                    commit()
                    initialConsonant = consonant
                }

                State.NO_FINAL -> finalConsonant = consonant
                State.FULL -> {
                    if (doubleFinalConsonantMap.containsKey(finalConsonant)) {
                        if (doubleFinalConsonantMap[finalConsonant]!!.containsKey(consonant)) {
                            finalConsonant = doubleFinalConsonantMap[finalConsonant]!![consonant]
                        } else {
                            commit()
                            initialConsonant = consonant
                        }
                    } else {
                        commit()
                        initialConsonant = consonant
                    }
                }
            }
        }

        fun addVowel(vowel: Char) {
            when (state) {
                State.NONE, State.SOLE_CONSONANT -> medialVowel = vowel
                State.SOLE_VOWEL, State.NO_FINAL, State.FULL -> {
                    commit()
                    initialConsonant = vowel
                }
            }
        }

        fun commit() {
            TODO("not implemented yet")
            initialConsonant = null
            medialVowel = null
            finalConsonant = null
        }
    }


    override val associatedKeyboardBinding: KeyboardHangulBinding =
        KeyboardHangulBinding.inflate(layoutInflater)

    override val buttonStrings = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("ㅣ", "ㆍ", "ㅡ", "←"),
        listOf("ㄱ", "ㄴ", "ㄹ", "␣"),
        listOf("ㅁ", "ㅅ", "ㅇ", "↑", "+"),
        listOf("!#1", "⊕", ".", ",", "?", " ! ", "↵")
    ).flatten()

    override val buttonSequence: Sequence<Button> = sequenceOf(
        associatedKeyboardBinding.hangulNumpad,
        associatedKeyboardBinding.hangulFirstLine,
        associatedKeyboardBinding.hangulSecondLine,
        associatedKeyboardBinding.hangulThirdLine,
        associatedKeyboardBinding.hangulFourthLine
    ).map { line: LinearLayout -> line.children }.flatten()
        .map { v: View -> extractButtonFromKeyboardItem(v) }.filterNotNull()

    init {
        initializeAllButtons()
    }

    override fun clickShift() {
        TODO("clickShift")
    }

    override fun clickBackspace() {
        TODO("clickBackspace")
    }


    override fun clickLanguage() {
        keyboardModeChangeListener.changeMode(KeyboardMode.ENGLISH)
    }

    override fun clickOther() {
        TODO("clickOther")
    }
}