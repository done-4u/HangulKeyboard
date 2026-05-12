package com.example.hangulkeyboard.keyboardview

internal sealed class ComposerResult {
    /** setComposingText(text) */
    data class Composing(val text: String) : ComposerResult()
    /** commitText(committed), then setComposingText(composing) */
    data class Committed(val committed: String, val composing: String) : ComposerResult()
    /** finishComposingText(), then setComposingText(composing) */
    data class Finished(val composing: String) : ComposerResult()
    /** setComposingText(flash), ignoreUpdateCount++, finishComposingText(), setComposingText(composing) */
    data class FlashFinish(val flash: String, val composing: String) : ComposerResult()
}

internal class HangulComposer {
    private var prevChar: HangulChar? = null
    private var currChar = HangulChar()

    val composingText: String
        get() = buildString {
            prevChar?.let { append(it) }
            if (!currChar.isEmpty()) append(currChar)
        }

    private fun syncState() {
        val prev = prevChar ?: return
        check(!prev.isEmpty()) { "prevChar must not be empty" }
        if (currChar.isEmpty()) {
            currChar = prev
            prevChar = null
        }
    }

    fun addPhoneme(phoneme: Char): ComposerResult {
        val overflow = currChar.addAtomicPhoneme(phoneme)
        syncState()

        if (overflow == null) {
            if (prevChar == null) return ComposerResult.Composing(composingText)
            // BOTH state
            val prev = checkNotNull(prevChar)
            val phonemeCurr = currChar.toPhoneme()
            if (phonemeCurr != null) {
                if (currChar.absorbFinalConsonant(prev)) {
                    val committed = prev.toString()
                    prevChar = null
                    return ComposerResult.Committed(committed, composingText)
                } else {
                    // phonemeCurr is the compound vowel currChar resolved to (e.g. ㆍ+ㅣ → ㅓ);
                    // permeate that into prev, then discard currChar which is now fully absorbed.
                    check(prev.vowelPermeate(phonemeCurr)) { "vowelPermeate failed unexpectedly" }
                    currChar = HangulChar()
                    syncState()
                }
            }
            return ComposerResult.Composing(composingText)
        }

        if (overflow.connected) {
            val oldPrev = prevChar
            prevChar = overflow.prev
            return if (oldPrev == null) {
                ComposerResult.Composing(composingText)
            } else {
                ComposerResult.Committed(oldPrev.toString(), composingText)
            }
        }

        // non-connected overflow: IC will finish the current composing text; start fresh
        prevChar = null
        return ComposerResult.Finished(composingText)
    }

    /** Returns false when empty — caller should send a real backspace instead. */
    fun erase(): Boolean {
        if (prevChar == null && currChar.isEmpty()) return false
        currChar.erase()
        syncState()
        return true
    }

    private fun applyTransform(transform: HangulChar.() -> OverflowResult?): ComposerResult {
        val overflow = currChar.transform()
        if (overflow != null) {
            val incoming = currChar
            currChar = overflow.prev
            val flash = composingText
            prevChar = null
            currChar = incoming
            syncState()
            return ComposerResult.FlashFinish(flash, composingText)
        }
        syncState()
        return ComposerResult.Composing(composingText)
    }

    fun applyDouble(): ComposerResult = applyTransform(HangulChar::double)
    fun applyStroke(): ComposerResult = applyTransform(HangulChar::stroke)

    fun reset() {
        prevChar = null
        currChar = HangulChar()
    }

    fun finish() {
        prevChar = null
        currChar = HangulChar()
    }
}
