package com.example.hangulkeyboard.keyboardview

// ic: initial consonant, mv: medial vowel, fc: final consonant

private val allConsonants = setOf(
    'ㄱ',
    'ㄲ',
    'ㄳ',
    'ㄴ',
    'ㄵ',
    'ㄶ',
    'ㄷ',
    'ㄸ',
    'ㄹ',
    'ㄺ',
    'ㄻ',
    'ㄼ',
    'ㄽ',
    'ㄾ',
    'ㄿ',
    'ㅀ',
    'ㅁ',
    'ㅂ',
    'ㅃ',
    'ㅄ',
    'ㅅ',
    'ㅆ',
    'ㅇ',
    'ㅈ',
    'ㅉ',
    'ㅊ',
    'ㅋ',
    'ㅌ',
    'ㅍ',
    'ㅎ'
)

private val allVowels = setOf(
    'ㅏ',
    'ㅐ',
    'ㅑ',
    'ㅒ',
    'ㅓ',
    'ㅔ',
    'ㅕ',
    'ㅖ',
    'ㅗ',
    'ㅘ',
    'ㅙ',
    'ㅚ',
    'ㅛ',
    'ㅜ',
    'ㅝ',
    'ㅞ',
    'ㅟ',
    'ㅠ',
    'ㅡ',
    'ㅢ',
    'ㅣ',
    'ㆍ',
    'ᆢ',
)

private val allAtomics = setOf(
    'ㅣ', 'ㆍ', 'ㅡ', 'ㄱ', 'ㄴ', 'ㄹ', 'ㅁ', 'ㅅ', 'ㅇ'
)

private val icIdxMap = mapOf(
    'ㄱ' to 0,
    'ㄲ' to 1,
    'ㄴ' to 2,
    'ㄷ' to 3,
    'ㄸ' to 4,
    'ㄹ' to 5,
    'ㅁ' to 6,
    'ㅂ' to 7,
    'ㅃ' to 8,
    'ㅅ' to 9,
    'ㅆ' to 10,
    'ㅇ' to 11,
    'ㅈ' to 12,
    'ㅉ' to 13,
    'ㅊ' to 14,
    'ㅋ' to 15,
    'ㅌ' to 16,
    'ㅍ' to 17,
    'ㅎ' to 18
)

private val idxICMap = icIdxMap.entries.associateBy({ it.value }) { it.key }

private val mvIdxMap = mapOf(
    'ㅏ' to 0,
    'ㅐ' to 1,
    'ㅑ' to 2,
    'ㅒ' to 3,
    'ㅓ' to 4,
    'ㅔ' to 5,
    'ㅕ' to 6,
    'ㅖ' to 7,
    'ㅗ' to 8,
    'ㅘ' to 9,
    'ㅙ' to 10,
    'ㅚ' to 11,
    'ㅛ' to 12,
    'ㅜ' to 13,
    'ㅝ' to 14,
    'ㅞ' to 15,
    'ㅟ' to 16,
    'ㅠ' to 17,
    'ㅡ' to 18,
    'ㅢ' to 19,
    'ㅣ' to 20,
)

private val idxMVMap = icIdxMap.entries.associateBy({ it.value }) { it.key }

private val fcIdxMap = mapOf(
    null to 0,
    'ㄱ' to 1,
    'ㄲ' to 2,
    'ㄳ' to 3,
    'ㄴ' to 4,
    'ㄵ' to 5,
    'ㄶ' to 6,
    'ㄷ' to 7,
    'ㄹ' to 8,
    'ㄺ' to 9,
    'ㄻ' to 10,
    'ㄼ' to 11,
    'ㄽ' to 12,
    'ㄾ' to 13,
    'ㄿ' to 14,
    'ㅀ' to 15,
    'ㅁ' to 16,
    'ㅂ' to 17,
    'ㅄ' to 18,
    'ㅅ' to 19,
    'ㅆ' to 20,
    'ㅇ' to 21,
    'ㅈ' to 22,
    'ㅊ' to 23,
    'ㅋ' to 24,
    'ㅌ' to 25,
    'ㅍ' to 26,
    'ㅎ' to 27,
)

private val idxFCMap = icIdxMap.entries.associateBy({ it.value }) { it.key }

private val doubleMap = mapOf(
    'ㄱ' to 'ㄲ',
    'ㄲ' to 'ㄱ',
    'ㄷ' to 'ㄸ',
    'ㄸ' to 'ㄷ',
    'ㅂ' to 'ㅃ',
    'ㅃ' to 'ㅂ',
    'ㅅ' to 'ㅆ',
    'ㅆ' to 'ㅅ',
    'ㅈ' to 'ㅉ',
    'ㅉ' to 'ㅈ',
    'ㆍ' to 'ᆢ',
    'ㅏ' to 'ㅑ',
    'ㅑ' to 'ㅏ',
    'ㅐ' to 'ㅒ',
    'ㅒ' to 'ㅐ',
    'ㅓ' to 'ㅕ',
    'ㅕ' to 'ㅓ',
    'ㅔ' to 'ㅖ',
    'ㅖ' to 'ㅔ',
    'ㅗ' to 'ㅛ',
    'ㅛ' to 'ㅗ',
    'ㅜ' to 'ㅠ',
    'ㅠ' to 'ㅜ',
)

private val strokeMap = mapOf(
    'ㄱ' to 'ㅋ',
    'ㄴ' to 'ㄷ',
    'ㄷ' to 'ㅌ',
    'ㅁ' to 'ㅂ',
    'ㅂ' to 'ㅍ',
    'ㅅ' to 'ㅈ',
    'ㅇ' to 'ㅎ',
    'ㅈ' to 'ㅊ',
    'ㅊ' to 'ㅅ',
    'ㅋ' to 'ㄱ',
    'ㅌ' to 'ㄴ',
    'ㅍ' to 'ㅁ',
    'ㅎ' to 'ㅇ',
    'ㅏ' to 'ㅐ',
    'ㅐ' to 'ㅘ',
    'ㅑ' to 'ㅒ',
    'ㅓ' to 'ㅔ',
    'ㅔ' to 'ㅝ',
    'ㅕ' to 'ㅖ',
    'ㅗ' to 'ㅚ',
    'ㅘ' to 'ㅙ',
    'ㅚ' to 'ㅘ',
    'ㅛ' to 'ㅙ',
    'ㅜ' to 'ㅟ',
    'ㅝ' to 'ㅞ',
    'ㅟ' to 'ㅝ',
    'ㅡ' to 'ㅢ',
    'ㅣ' to 'ㅢ'
)

private val cmvBuildMap = mapOf(
    'ㆍ' to mapOf('ㆍ' to 'ᆢ', 'ㅡ' to 'ㅗ', 'ㅣ' to 'ㅓ'),
    'ᆢ' to mapOf('ㅡ' to 'ㅛ', 'ㅣ' to 'ㅕ'),
    'ㅏ' to mapOf('ㆍ' to 'ㅑ', 'ㅣ' to 'ㅐ'),
    'ㅑ' to mapOf('ㅣ' to 'ㅒ'),
    'ㅓ' to mapOf('ㅣ' to 'ㅔ'),
    'ㅕ' to mapOf('ㅣ' to 'ㅖ'),
    'ㅗ' to mapOf('ㅣ' to 'ㅚ'),
    'ㅘ' to mapOf('ㅣ' to 'ㅙ'),
    'ㅚ' to mapOf('ㆍ' to 'ㅘ'),
    'ㅜ' to mapOf('ㆍ' to 'ㅠ', 'ㅣ' to 'ㅟ'),
    'ㅝ' to mapOf('ㅣ' to 'ㅞ'),
    'ㅠ' to mapOf('ㅣ' to 'ㅝ'),
    'ㅡ' to mapOf('ㆍ' to 'ㅜ', 'ㅣ' to 'ㅢ'),
    'ㅣ' to mapOf('ㆍ' to 'ㅏ')
)

private val cmvBreakMap = mapOf(
    'ㅐ' to 'ㅏ',
    'ㅒ' to 'ㅑ',
    'ㅔ' to 'ㅓ',
    'ㅖ' to 'ㅕ',
    'ㅘ' to 'ㅗ',
    'ㅙ' to 'ㅘ',
    'ㅚ' to 'ㅗ',
    'ㅝ' to 'ㅜ',
    'ㅞ' to 'ㅝ',
    'ㅟ' to 'ㅜ',
    'ㅢ' to 'ㅡ',
)

private val cfcBuildMap = mapOf(
    'ㄱ' to mapOf('ㅅ' to 'ㄳ'), 'ㄴ' to mapOf('ㅈ' to 'ㄵ', 'ㅎ' to 'ㄶ'), 'ㄹ' to mapOf(
        'ㄱ' to 'ㄺ', 'ㅁ' to 'ㄻ', 'ㅂ' to 'ㄼ', 'ㅅ' to 'ㄽ', 'ㅌ' to 'ㄾ', 'ㅍ' to 'ㄿ', 'ㅎ' to 'ㅀ'
    ), 'ㅂ' to mapOf('ㅅ' to 'ㅄ')
)

private val cfcBreakMap = mapOf(
    'ㄳ' to Pair('ㄱ', 'ㅅ'),
    'ㄵ' to Pair('ㄴ', 'ㅈ'),
    'ㄶ' to Pair('ㄴ', 'ㅎ'),
    'ㄺ' to Pair('ㄹ', 'ㄱ'),
    'ㄻ' to Pair('ㄹ', 'ㅁ'),
    'ㄼ' to Pair('ㄹ', 'ㅂ'),
    'ㄽ' to Pair('ㄹ', 'ㅅ'),
    'ㄾ' to Pair('ㄹ', 'ㅌ'),
    'ㄿ' to Pair('ㄹ', 'ㅍ'),
    'ㅀ' to Pair('ㄹ', 'ㅎ'),
    'ㅄ' to Pair('ㅂ', 'ㅅ')
)

private val expectedAtomicsForCfcMap = mapOf(
    'ㄱ' to setOf('ㅅ'),
    'ㄴ' to setOf('ㅅ', 'ㅇ'),
    'ㄹ' to setOf('ㄱ', 'ㄴ', 'ㅁ', 'ㅅ', 'ㅇ'),
    'ㅂ' to setOf('ㅅ')
)

// unicode constants
private const val NUM_MV = 21
private const val NUM_FC = 28
private const val HANGUL_UNICODE_BASE = 0xAC00
private const val HANGUL_UNICODE_LAST = 0xD7A3


data class AddOverflow(val connected: Boolean, val prev: HangulChar)


class HangulChar {
    private enum class State { NONE, SOLE_CONSONANT, SOLE_VOWEL, NO_FINAL, FULL }

    private var state: State = State.NONE
    private var initialConsonant: Char? = null
        set(value) {
            field = value
            reflex()
        }
    private var medialVowel: Char? = null
        set(value) {
            field = value
            reflex()
        }
    private var finalConsonant: Char? = null
        set(value) {
            field = value
            reflex()
        }

    // update state
    private fun reflex() {
        assert(initialConsonant == null || allConsonants.contains(initialConsonant!!))
        assert(medialVowel == null || allVowels.contains(medialVowel!!))
        assert(finalConsonant == null || allConsonants.contains(finalConsonant!!))

        state = if (finalConsonant == null) {
            if (medialVowel == null) {
                if (initialConsonant == null) State.NONE
                else State.SOLE_CONSONANT
            } else {
                if (initialConsonant == null) State.SOLE_VOWEL
                else State.NO_FINAL
            }
        } else {
            if (initialConsonant == null || medialVowel == null) throw IllegalStateException()
            else State.FULL
        }
    }

    private fun <T> categorize(
        phoneme: Char, caseConsonant: (Char) -> T, caseVowel: (Char) -> T
    ): T {
        return if (allConsonants.contains(phoneme)) {
            caseConsonant(phoneme)
        } else {
            assert(allVowels.contains(phoneme))
            caseVowel(phoneme)
        }
    }

    fun addAtomicPhoneme(phoneme: Char): AddOverflow? {
        // adding 'ㅣ', 'ㆍ', 'ㅡ', 'ㄱ', 'ㄴ', 'ㄹ', 'ㅁ', 'ㅅ', 'ㅇ'
        if (!allAtomics.contains(phoneme)) {
            throw IllegalArgumentException()
        }

        return categorize(phoneme, ::addAtomicConsonant, ::addAtomicVowel)
    }

    private fun addAtomicConsonant(consonant: Char): AddOverflow? {
        // adding 'ㄱ', 'ㄴ', 'ㄹ', 'ㅁ', 'ㅅ', 'ㅇ'
        var ret: AddOverflow? = null

        when (state) {
            State.NONE -> {
                // "" + ㄱ => ㄱ
                initialConsonant = consonant
            }

            State.SOLE_CONSONANT, State.SOLE_VOWEL -> {
                // ㄱ + ㄱ => ㄱㄱ
                // ㄹ + ㄱ => ㄹㄱ (not complex consonants)
                // ㅏ + ㄴ => ㅏㄴ
                ret = AddOverflow(false, copyAndReset())
                initialConsonant = consonant
            }

            State.NO_FINAL -> {
                // 가 + ㄱ => 각
                // all atomic consonants can be final consonants
                finalConsonant = consonant
            }

            State.FULL -> {
                if (cfcBuildMap.containsKey(finalConsonant)) {
                    val cfc = cfcBuildMap[finalConsonant]?.get(consonant)
                    if (cfc != null) {
                        // 달 + ㄱ => 닭
                        finalConsonant = cfc
                    } else {
                        if (expectedAtomicsForCfcMap[finalConsonant]!!.contains(consonant)) {
                            // 안 + ㅇ => 안ㅇ (it may be "않" later)
                            ret = AddOverflow(true, copyAndReset())
                            initialConsonant = consonant
                        } else {
                            // 안 + ㄱ => 안ㄱ (it can never reach "않" or "앉")
                            ret = AddOverflow(false, copyAndReset())
                            initialConsonant = consonant
                        }
                    }
                } else {
                    // 맛 + ㅇ => 맛ㅇ
                    ret = AddOverflow(false, copyAndReset())
                    initialConsonant = consonant
                }
            }
        }

        return ret
    }

    private fun addAtomicVowel(vowel: Char): AddOverflow? {
        // adding 'ㅣ', 'ㆍ', 'ㅡ'
        var ret: AddOverflow? = null

        when (state) {
            State.NONE -> {
                // "" + ㅣ => ㅣ
                medialVowel = vowel
            }

            State.SOLE_CONSONANT -> {
                if (mvIdxMap.containsKey(vowel)) {
                    // ㄱ + ㅡ => 그
                    medialVowel = vowel
                } else {
                    // ㄱ + ㆍ => ㄱㆍ (can be "거" later)
                    ret = AddOverflow(true, copyAndReset())
                    medialVowel = vowel
                }
            }

            State.SOLE_VOWEL, State.NO_FINAL -> {
                val cmv = cmvBuildMap[medialVowel]?.get(vowel)
                if (cmv == null) {
                    // ㅣ + ㅣ => ㅣㅣ
                    ret = AddOverflow(false, copyAndReset())
                    medialVowel = vowel
                } else {
                    // 오 + ㅣ => 외
                    medialVowel = cmv
                }
            }

            State.FULL -> {
                if (!mvIdxMap.containsKey(vowel)) {
                    ret = AddOverflow(true, copyAndReset())
                } else {
                    val pair = cfcBreakMap[finalConsonant]
                    if (pair == null) {
                        // 울 + ㅣ => 우리
                        val foo = finalConsonant
                        finalConsonant = null
                        ret = AddOverflow(false, copyAndReset())
                        initialConsonant = foo
                    } else {
                        // 몺 + ㅣ => 몹시
                        val left = pair.first
                        val moving = pair.second
                        finalConsonant = left
                        ret = AddOverflow(false, copyAndReset())
                        initialConsonant = moving
                    }
                }
                medialVowel = vowel
            }
        }

        return ret
    }

    fun vowelPermeate(vowel: Char): Boolean {
        assert(allVowels.contains(vowel))
        when (state) {
            State.NONE -> {
                // "" + ㅏ => ㅏ
                medialVowel = vowel
                return true
            }

            State.SOLE_CONSONANT -> {
                // ㄱ + ㅗ => 고
                if (mvIdxMap.containsKey(vowel)) {
                    medialVowel = vowel
                    return true
                }
                return false
            }

            State.SOLE_VOWEL -> {
                // ㅜ + ㅣ => ㅟ
                val cmv = cmvBuildMap[medialVowel]?.get(vowel) ?: return false
                medialVowel = cmv
                return true
            }

            else -> return false
        }
    }

    fun absorbFinalConsonant(prev: HangulChar): Boolean {
        if (state != State.SOLE_VOWEL || prev.state != State.FULL) {
            return false
        }

        val foo = cfcBreakMap[prev.finalConsonant]
        if (foo == null) {
            initialConsonant = prev.finalConsonant
            prev.finalConsonant = null
        } else {
            prev.finalConsonant = foo.first
            initialConsonant = foo.second
        }

        return true
    }

    fun erase(): Boolean {
        if (state == State.NONE) {
            return false
        }

        if (finalConsonant != null) {
            finalConsonant = cfcBreakMap[finalConsonant]?.first
        } else if (medialVowel != null) {
            medialVowel = cmvBreakMap[medialVowel]
        } else {
            assert(initialConsonant != null)
            initialConsonant = null
        }

        return true

    }

    private fun replaceLast(replaceMap: Map<Char, Char>): AddOverflow? {
        when (state) {
            State.NONE -> return null

            State.SOLE_CONSONANT -> {
                if (replaceMap.containsKey(initialConsonant)) {
                    initialConsonant = replaceMap[initialConsonant]
                }
                return null
            }

            State.SOLE_VOWEL, State.NO_FINAL -> {
                if (replaceMap.containsKey(medialVowel)) {
                    medialVowel = replaceMap[medialVowel]
                }
                return null
            }

            State.FULL -> {
                if (replaceMap.containsKey(finalConsonant)) {
                    val foo = replaceMap[finalConsonant]
                    if (fcIdxMap.containsKey(foo)) {
                        // 당 + (stroke) -> 닿
                        finalConsonant = foo
                        return null
                    } else {
                        // 갇 + (double) -> 가ㄸ
                        finalConsonant = null
                        val ret = AddOverflow(false, copyAndReset())
                        initialConsonant = foo
                        return ret
                    }
                }

                return null
            }
        }
    }

    fun stroke(): AddOverflow? {
        return replaceLast(strokeMap)
    }

    fun double(): AddOverflow? {
        return replaceLast(doubleMap)
    }

    fun isNull(): Boolean {
        return state == State.NONE
    }

    // If possible, return it. Else return null.
    fun toPhoneme(): Char? {
        return when (state) {
            State.SOLE_CONSONANT -> initialConsonant
            State.SOLE_VOWEL -> medialVowel
            else -> null
        }
    }

    fun toChar(): Char? {
        return when (state) {
            State.NONE -> null
            State.SOLE_CONSONANT -> initialConsonant
            State.SOLE_VOWEL -> medialVowel
            State.NO_FINAL -> {
                var ret = HANGUL_UNICODE_BASE
                ret += icIdxMap[initialConsonant]!! * NUM_MV * NUM_FC
                ret += mvIdxMap[medialVowel]!! * NUM_FC
                ret.toChar()
            }

            State.FULL -> {
                var ret = HANGUL_UNICODE_BASE
                ret += icIdxMap[initialConsonant]!! * NUM_MV * NUM_FC
                ret += mvIdxMap[medialVowel]!! * NUM_FC
                ret += fcIdxMap[finalConsonant]!!
                ret.toChar()
            }
        }
    }

    override fun toString(): String {
        val char = toChar()
        return char?.toString() ?: ""
    }

    private fun copy(): HangulChar {
        val ret = HangulChar()
        ret.initialConsonant = initialConsonant
        ret.medialVowel = medialVowel
        ret.finalConsonant = finalConsonant
        ret.reflex()
        return ret
    }

    private fun reset() {
        // DO NOT change the order! (for state legibility)
        finalConsonant = null
        medialVowel = null
        initialConsonant = null
    }

    private fun copyAndReset(): HangulChar {
        val ret = copy()
        reset()
        return ret
    }

    companion object {
        fun ofChar(char: Char): HangulChar {
            val ret = HangulChar()
            if (allConsonants.contains(char)) {
                ret.initialConsonant = char
            } else if (allVowels.contains(char)) {
                ret.medialVowel = char
            } else if (char.code in HANGUL_UNICODE_BASE..HANGUL_UNICODE_LAST) {
                ret.initialConsonant = idxICMap[char.code / (NUM_MV * NUM_FC)]
                ret.medialVowel = idxMVMap[(char.code % (NUM_MV * NUM_FC)) / NUM_FC]
                ret.finalConsonant = idxFCMap[char.code % NUM_FC]
            }
            return ret
        }
    }
}
