package com.example.hangulkeyboard.keyboardview

import android.media.AudioManager

enum class FunctionalKey {
    // OTHER is a bonus slot; in this package, it is used for adding stroke in Korean consonants.
    SHIFT, BACKSPACE, SPECIAL, LANGUAGE, SPACE, ENTER, OTHER, NONE;

    fun getFX(): Int = when (this) {
        BACKSPACE -> AudioManager.FX_KEYPRESS_DELETE
        SPACE -> AudioManager.FX_KEYPRESS_SPACEBAR
        ENTER -> AudioManager.FX_KEYPRESS_RETURN
        else -> AudioManager.FX_KEYPRESS_STANDARD
    }

    companion object {
        const val LABEL_SHIFT = "↑"
        const val LABEL_BACKSPACE = "←"
        const val LABEL_SPECIAL = "!@#"
        const val LABEL_LANGUAGE = "⊕"
        const val LABEL_SPACE = "␣"
        const val LABEL_ENTER = "↵"
        const val LABEL_OTHER = "′"

        fun eval(text: CharSequence) = when (text.toString()) {
            LABEL_SHIFT -> SHIFT
            LABEL_BACKSPACE -> BACKSPACE
            LABEL_SPECIAL -> SPECIAL
            LABEL_LANGUAGE -> LANGUAGE
            LABEL_SPACE -> SPACE
            LABEL_ENTER -> ENTER
            LABEL_OTHER -> OTHER
            else -> NONE
        }
    }
}
