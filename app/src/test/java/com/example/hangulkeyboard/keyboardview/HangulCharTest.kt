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
}
