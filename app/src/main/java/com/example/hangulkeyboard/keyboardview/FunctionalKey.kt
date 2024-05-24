package com.example.hangulkeyboard.keyboardview

import android.media.AudioManager

enum class FunctionalKey {
    // OTHER is an bonus slot; in this package, it is used for adding stroke in Korean consonants.
    SHIFT, BACKSPACE, SPECIAL, LANGUAGE, SPACE, ENTER, OTHER, NONE;

    fun getFX(): Int = when (this) {
        BACKSPACE -> AudioManager.FX_KEYPRESS_DELETE
        SPACE -> AudioManager.FX_KEYPRESS_SPACEBAR
        ENTER -> AudioManager.FX_KEYPRESS_RETURN
        else -> AudioManager.FX_KEYPRESS_STANDARD
    }

    // To avoid reserved key symbols, inject spaces in both sides
    companion object {
        fun eval(text: CharSequence) = when (text[0]) {
            '↑' -> SHIFT
            '←' -> BACKSPACE
            '!' -> SPECIAL
            '⊕' -> LANGUAGE
            '␣' -> SPACE
            '↵' -> ENTER
            '+' -> OTHER
            else -> NONE
        }
    }
}