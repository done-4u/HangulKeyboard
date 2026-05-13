package com.example.hangulkeyboard.keyboardview

import org.junit.Assert.*
import org.junit.Test

class HangulCharTest {
    // ── isEmpty ──────────────────────────────────────────────────────────────

    @Test
    fun `new instance is empty`() {
        assertTrue(HangulChar().isEmpty())
    }

    @Test
    fun `not empty after phoneme added`() {
        assertFalse(HangulChar.fromChar('ㄱ').isEmpty())
    }

    // ── addAtomicConsonant ───────────────────────────────────────────────────

    @Test
    fun `consonant alone becomes sole consonant`() {
        assertEquals('ㄱ', HangulChar.fromChar('ㄱ').toChar())
    }

    @Test
    fun `consonant added to sole consonant`() {
        val h = HangulChar.fromChar('ㄹ')
        val overflow = h.addAtomicPhoneme('ㄱ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('ㄹ', overflow.prev.toChar())
        assertEquals('ㄱ', h.toChar())
    }

    @Test
    fun `consonant added to sole vowel`() {
        val h = HangulChar.fromChar('ㅏ')
        val overflow = h.addAtomicPhoneme('ㄴ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('ㅏ', overflow.prev.toChar())
        assertEquals('ㄴ', h.toChar())
    }

    @Test
    fun `consonant added to no-final`() {
        val h = HangulChar.fromChar('가')
        val overflow = h.addAtomicPhoneme('ㄱ')
        assertNull(overflow)
        assertEquals('각', h.toChar())
    }

    @Test
    fun `consonant added to full, resulting complex final consonant`() {
        val h = HangulChar.fromChar('달')
        val overflow = h.addAtomicPhoneme('ㄱ')
        assertNull(overflow)
        assertEquals('닭', h.toChar())
    }

    @Test
    fun `consonant added to full, resulting connected overflow`() {
        val h = HangulChar.fromChar('안')
        val overflow = h.addAtomicPhoneme('ㅇ')
        assertNotNull(overflow)
        assertTrue(overflow!!.connected)
        assertEquals('안', overflow.prev.toChar())
        assertEquals('ㅇ', h.toChar())
    }

    @Test
    fun `consonant added to full, resulting disconnected overflow - case A`() {
        val h = HangulChar.fromChar('안')
        val overflow = h.addAtomicPhoneme('ㄱ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('안', overflow.prev.toChar())
        assertEquals('ㄱ', h.toChar())
    }

    @Test
    fun `consonant added to full, resulting disconnected overflow - case B`() {
        val h = HangulChar.fromChar('맛')
        val overflow = h.addAtomicPhoneme('ㅇ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('맛', overflow.prev.toChar())
        assertEquals('ㅇ', h.toChar())
    }

    // ── addAtomicPhoneme: vowel paths ─────────────────────────────────────────

    @Test
    fun `vowel alone becomes sole vowel`() {
        assertEquals('ㅣ', HangulChar.fromChar('ㅣ').toChar())
    }

    @Test
    fun `vowel added to sole consonant`() {
        val h = HangulChar.fromChar('ㄱ')
        val overflow = h.addAtomicPhoneme('ㅡ')
        assertNull(overflow)
        assertEquals('그', h.toChar())
    }

    @Test
    fun `vowel added to sole vowel, and cannot be merged`() {
        val h = HangulChar.fromChar('ㅣ')
        val overflow = h.addAtomicPhoneme('ㅣ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('ㅣ', overflow.prev.toChar())
        assertEquals('ㅣ', h.toChar())
    }

    @Test
    fun `vowel added to sole vowel, and can be merged`() {
        val h = HangulChar.fromChar('ㅗ')
        val overflow = h.addAtomicPhoneme('ㅣ')
        assertNull(overflow)
        assertEquals('ㅚ', h.toChar())
    }

    @Test
    fun `vowel added to no-final, and cannot be merged`() {
        val h = HangulChar.fromChar('기')
        val overflow = h.addAtomicPhoneme('ㅣ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('기', overflow.prev.toChar())
        assertEquals('ㅣ', h.toChar())
    }

    @Test
    fun `vowel added to no-final, and can be merged`() {
        val h = HangulChar.fromChar('오')
        val overflow = h.addAtomicPhoneme('ㅣ')
        assertNull(overflow)
        assertEquals('외', h.toChar())
    }

    @Test
    fun `vowel added to full with simple final consonant`() {
        val h = HangulChar.fromChar('울')
        val overflow = h.addAtomicPhoneme('ㅣ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('우', overflow.prev.toChar())
        assertEquals('리', h.toChar())
    }

    @Test
    fun `vowel added to full with complex final consonant`() {
        val h = HangulChar.fromChar('몺')
        val overflow = h.addAtomicPhoneme('ㅣ')
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('몹', overflow.prev.toChar())
        assertEquals('시', h.toChar())
    }

    @Test
    fun `vowel added to sole consonant, connected overflow`() {
        val h = HangulChar.fromChar('ㄱ')
        val overflow = h.addAtomicPhoneme('ㆍ')
        assertNotNull(overflow)
        assertTrue(overflow!!.connected)
        assertEquals('ㄱ', overflow.prev.toChar())
        assertEquals('ㆍ', h.toChar())
    }

    // ── vowelPermeate ─────────────────────────────────────────────────────────

    @Test
    fun `vowelPermeate sets vowel on empty`() {
        val h = HangulChar()
        assertTrue(h.vowelPermeate('ㅏ'))
        assertEquals('ㅏ', h.toChar())
    }

    @Test
    fun `vowelPermeate on sole consonant`() {
        val h = HangulChar.fromChar('ㄱ')
        assertTrue(h.vowelPermeate('ㅗ'))
        assertEquals('고', h.toChar())
    }

    @Test
    fun `vowelPermeate builds compound vowel on sole vowel`() {
        val h = HangulChar.fromChar('ㅜ')
        assertTrue(h.vowelPermeate('ㅣ'))
        assertEquals('ㅟ', h.toChar())
    }

    @Test
    fun `vowelPermeate returns false on no-final`() {
        val h = HangulChar.fromChar('가')
        assertFalse(h.vowelPermeate('ㅗ'))
    }

    @Test
    fun `vowelPermeate returns false on full`() {
        val h = HangulChar.fromChar('각')
        assertFalse(h.vowelPermeate('ㅗ'))
    }

    // ── absorbFinalConsonant ──────────────────────────────────────────────────

    @Test
    fun `absorbFinalConsonant takes simple final consonant`() {
        val prev = HangulChar.fromChar('각')
        val curr = HangulChar.fromChar('ㅡ')
        assertTrue(curr.absorbFinalConsonant(prev))
        assertEquals('가', prev.toChar())
        assertEquals('그', curr.toChar())
    }

    @Test
    fun `absorbFinalConsonant splits complex final consonant`() {
        val prev = HangulChar.fromChar('닭')
        val curr = HangulChar.fromChar('ㅡ')
        assertTrue(curr.absorbFinalConsonant(prev))
        assertEquals('달', prev.toChar())
        assertEquals('그', curr.toChar())
    }

    @Test
    fun `absorbFinalConsonant returns false when curr not sole vowel`() {
        val prev = HangulChar.fromChar('각')
        val curr = HangulChar.fromChar('ㄱ')
        assertFalse(curr.absorbFinalConsonant(prev))
    }

    @Test
    fun `absorbFinalConsonant returns false when prev not full`() {
        val prev = HangulChar.fromChar('가')
        val curr = HangulChar.fromChar('ㅡ')
        assertFalse(curr.absorbFinalConsonant(prev))
    }

    // ── erase ─────────────────────────────────────────────────────────────────

    @Test
    fun `erase returns false when empty`() {
        assertFalse(HangulChar().erase())
    }

    @Test
    fun `erase removes sole consonant`() {
        val h = HangulChar.fromChar('ㄱ')
        assertTrue(h.erase())
        assertTrue(h.isEmpty())
    }

    @Test
    fun `erase removes sole vowel`() {
        val h = HangulChar.fromChar('ㅏ')
        assertTrue(h.erase())
        assertTrue(h.isEmpty())
    }

    @Test
    fun `erase removes final consonant from full`() {
        val h = HangulChar.fromChar('각')
        assertTrue(h.erase())
        assertEquals('가', h.toChar())
    }

    @Test
    fun `erase splits complex final consonant`() {
        val h = HangulChar.fromChar('닭')
        assertTrue(h.erase())
        assertEquals('달', h.toChar())
    }

    @Test
    fun `erase removes medial vowel from no-final`() {
        val h = HangulChar.fromChar('가')
        assertTrue(h.erase())
        assertEquals('ㄱ', h.toChar())
    }

    @Test
    fun `erase simplifies compound medial vowel`() {
        val h = HangulChar.fromChar('ㅟ')
        assertTrue(h.erase())
        assertEquals('ㅜ', h.toChar())
    }

    // ── stroke ────────────────────────────────────────────────────────────────

    @Test
    fun `stroke returns null on empty`() {
        assertNull(HangulChar().stroke())
    }

    @Test
    fun `stroke transforms sole consonant`() {
        val h = HangulChar.fromChar('ㄱ')
        assertNull(h.stroke())
        assertEquals('ㅋ', h.toChar())
    }

    @Test
    fun `stroke transforms medial vowel on no-final`() {
        val h = HangulChar.fromChar('가')
        assertNull(h.stroke())
        assertEquals('개', h.toChar())
    }

    @Test
    fun `stroke transforms final consonant to valid final consonant`() {
        val h = HangulChar.fromChar('당')
        assertNull(h.stroke())
        assertEquals('닿', h.toChar())
    }

    // ── double ────────────────────────────────────────────────────────────────

    @Test
    fun `double returns null on empty`() {
        assertNull(HangulChar().double())
    }

    @Test
    fun `double transforms sole consonant`() {
        val h = HangulChar.fromChar('ㄱ')
        assertNull(h.double())
        assertEquals('ㄲ', h.toChar())
    }

    @Test
    fun `double transforms final consonant to valid final consonant`() {
        val h = HangulChar.fromChar('갓')
        assertNull(h.double())
        assertEquals('갔', h.toChar())
    }

    @Test
    fun `double overflows when transformed final consonant is invalid`() {
        val h = HangulChar.fromChar('갇')
        val overflow = h.double()
        assertNotNull(overflow)
        assertFalse(overflow!!.connected)
        assertEquals('가', overflow.prev.toChar())
        assertEquals('ㄸ', h.toChar())
    }
}
