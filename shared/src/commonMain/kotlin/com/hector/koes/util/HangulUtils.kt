package com.hector.koes.util

object HangulUtils {

    private const val HANGUL_BASE = 0xAC00
    private const val HANGUL_END = 0xD7A3

    private val CHOSEONG = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private val JUNGSEONG = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
        'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅡ', 'ㅢ', 'ㅣ'
    )

    private val JONGSEONG = charArrayOf(
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
        'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    // Descomposición de vocales compuestas a Jamos elementales
    private val COMPOUND_VOWELS = mapOf(
        'ㅘ' to listOf('ㅗ', 'ㅏ'),
        'ㅙ' to listOf('ㅗ', 'ㅐ'),
        'ㅚ' to listOf('ㅗ', 'ㅣ'),
        'ㅝ' to listOf('ㅜ', 'ㅓ'),
        'ㅞ' to listOf('ㅜ', 'ㅔ'),
        'ㅟ' to listOf('ㅜ', 'ㅣ'),
        'ㅢ' to listOf('ㅡ', 'ㅣ'),
        'ㅐ' to listOf('ㅏ', 'ㅣ'),
        'ㅔ' to listOf('ㅓ', 'ㅣ'),
        'ㅒ' to listOf('ㅑ', 'ㅣ'),
        'ㅖ' to listOf('ㅕ', 'ㅣ')
    )

    // Descomposición de consonantes compuestas / batchims a Jamos elementales
    private val COMPOUND_CONSONANTS = mapOf(
        'ㄳ' to listOf('ㄱ', 'ㅅ'),
        'ㄵ' to listOf('ㄴ', 'ㅈ'),
        'ㄶ' to listOf('ㄴ', 'ㅎ'),
        'ㄺ' to listOf('ㄹ', 'ㄱ'),
        'ㄻ' to listOf('ㄹ', 'ㅁ'),
        'ㄼ' to listOf('ㄹ', 'ㅂ'),
        'ㄽ' to listOf('ㄹ', 'ㅅ'),
        'ㄾ' to listOf('ㄹ', 'ㅌ'),
        'ㄿ' to listOf('ㄹ', 'ㅍ'),
        'ㅀ' to listOf('ㄹ', 'ㅎ'),
        'ㅄ' to listOf('ㅂ', 'ㅅ'),
        'ㄲ' to listOf('ㄱ', 'ㄱ'),
        'ㄸ' to listOf('ㄷ', 'ㄷ'),
        'ㅃ' to listOf('ㅂ', 'ㅂ'),
        'ㅆ' to listOf('ㅅ', 'ㅅ'),
        'ㅉ' to listOf('ㅈ', 'ㅈ')
    )

    /**
     * Descompone un texto en coreano (sílabas Hangul o Jamos sueltos) en una lista de Jamos elementales.
     */
    fun decomposeToJamos(text: String): List<Char> {
        val result = mutableListOf<Char>()
        for (ch in text) {
            val code = ch.code
            if (code in HANGUL_BASE..HANGUL_END) {
                val index = code - HANGUL_BASE
                val choseongIdx = index / (21 * 28)
                val jungseongIdx = (index % (21 * 28)) / 28
                val jongseongIdx = index % 28

                val choseongChar = CHOSEONG.getOrElse(choseongIdx) { ' ' }
                val jungseongChar = JUNGSEONG.getOrElse(jungseongIdx) { ' ' }
                val jongseongChar = JONGSEONG.getOrElse(jongseongIdx) { ' ' }

                result.addAll(COMPOUND_CONSONANTS[choseongChar] ?: listOf(choseongChar))
                result.addAll(COMPOUND_VOWELS[jungseongChar] ?: listOf(jungseongChar))

                if (jongseongIdx > 0 && jongseongChar != ' ') {
                    result.addAll(COMPOUND_CONSONANTS[jongseongChar] ?: listOf(jongseongChar))
                }
            } else {
                val compoundVowel = COMPOUND_VOWELS[ch]
                val compoundConsonant = COMPOUND_CONSONANTS[ch]
                when {
                    compoundVowel != null -> result.addAll(compoundVowel)
                    compoundConsonant != null -> result.addAll(compoundConsonant)
                    else -> result.add(ch)
                }
            }
        }
        return result
    }

    /**
     * Determina la validez de cada carácter escrito por el usuario en tiempo real.
     * Retorna un BooleanArray donde cada elemento indica si el carácter en esa posición es correcto.
     * Si ocurre un error en cualquier punto, ese carácter y los posteriores se marcan como incorrectos (false).
     */
    fun getCharacterValidation(typedText: String, targetText: String): BooleanArray {
        val isCorrect = BooleanArray(typedText.length)
        if (typedText.isEmpty() || targetText.isEmpty()) return isCorrect

        try {
            val targetJamos = decomposeToJamos(targetText)
            var targetJamoOffset = 0
            var hasEncounteredError = false

            for (i in typedText.indices) {
                if (hasEncounteredError) {
                    isCorrect[i] = false
                    continue
                }

                val typedChar = typedText[i]
                val typedCharJamos = decomposeToJamos(typedChar.toString())

                val remainingTargetJamosCount = targetJamos.size - targetJamoOffset
                if (remainingTargetJamosCount <= 0) {
                    isCorrect[i] = false
                    hasEncounteredError = true
                    continue
                }

                val remainingTargetJamos = targetJamos.subList(targetJamoOffset, targetJamos.size)

                if (typedCharJamos.isNotEmpty() &&
                    typedCharJamos.size <= remainingTargetJamos.size &&
                    typedCharJamos == remainingTargetJamos.subList(0, typedCharJamos.size)
                ) {
                    isCorrect[i] = true
                    targetJamoOffset += typedCharJamos.size
                } else {
                    isCorrect[i] = false
                    hasEncounteredError = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isCorrect
    }
}
