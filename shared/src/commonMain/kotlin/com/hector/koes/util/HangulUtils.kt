package com.hector.koes.util


object HangulUtils {

    private const val HANGUL_BASE =
        0xAC00

    private const val HANGUL_END =
        0xD7A3


    private val CHOSEONG =
        charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ',
            'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ',
            'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )


    private val JUNGSEONG =
        charArrayOf(
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ',
            'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ',
            'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ',
            'ㅛ',
            'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ',
            'ㅠ',
            'ㅡ', 'ㅢ',
            'ㅣ'
        )


    private val JONGSEONG =
        charArrayOf(
            ' ',
            'ㄱ',
            'ㄲ',
            'ㄳ',
            'ㄴ',
            'ㄵ',
            'ㄶ',
            'ㄷ',
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
            'ㅄ',
            'ㅅ',
            'ㅆ',
            'ㅇ',
            'ㅈ',
            'ㅊ',
            'ㅋ',
            'ㅌ',
            'ㅍ',
            'ㅎ'
        )


    /*
     * Vocales compuestas modernas.
     */
    private val COMPOUND_VOWELS =
        mapOf(

            Pair(
                'ㅗ',
                'ㅏ'
            ) to 'ㅘ',

            Pair(
                'ㅗ',
                'ㅐ'
            ) to 'ㅙ',

            Pair(
                'ㅗ',
                'ㅣ'
            ) to 'ㅚ',

            Pair(
                'ㅜ',
                'ㅓ'
            ) to 'ㅝ',

            Pair(
                'ㅜ',
                'ㅔ'
            ) to 'ㅞ',

            Pair(
                'ㅜ',
                'ㅣ'
            ) to 'ㅟ',

            Pair(
                'ㅡ',
                'ㅣ'
            ) to 'ㅢ'
        )


    private val COMPOUND_VOWELS_REVERSE =
        mapOf(
            'ㅘ' to Pair('ㅗ', 'ㅏ'),
            'ㅙ' to Pair('ㅗ', 'ㅐ'),
            'ㅚ' to Pair('ㅗ', 'ㅣ'),

            'ㅝ' to Pair('ㅜ', 'ㅓ'),
            'ㅞ' to Pair('ㅜ', 'ㅔ'),
            'ㅟ' to Pair('ㅜ', 'ㅣ'),

            'ㅢ' to Pair('ㅡ', 'ㅣ')
        )


    /*
     * Batchim compuestos.
     */
    private val COMPOUND_FINALS =
        mapOf(

            Pair(
                'ㄱ',
                'ㅅ'
            ) to 'ㄳ',

            Pair(
                'ㄴ',
                'ㅈ'
            ) to 'ㄵ',

            Pair(
                'ㄴ',
                'ㅎ'
            ) to 'ㄶ',

            Pair(
                'ㄹ',
                'ㄱ'
            ) to 'ㄺ',

            Pair(
                'ㄹ',
                'ㅁ'
            ) to 'ㄻ',

            Pair(
                'ㄹ',
                'ㅂ'
            ) to 'ㄼ',

            Pair(
                'ㄹ',
                'ㅅ'
            ) to 'ㄽ',

            Pair(
                'ㄹ',
                'ㅌ'
            ) to 'ㄾ',

            Pair(
                'ㄹ',
                'ㅍ'
            ) to 'ㄿ',

            Pair(
                'ㄹ',
                'ㅎ'
            ) to 'ㅀ',

            Pair(
                'ㅂ',
                'ㅅ'
            ) to 'ㅄ'
        )


    private val COMPOUND_FINALS_REVERSE =
        mapOf(
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


    /*
     * ============================================================
     * CREAR SÍLABA
     * ============================================================
     */
    private fun createSyllable(
        cho: Int,
        jung: Int,
        jong: Int = 0
    ): Char {

        val code =
            HANGUL_BASE +
                    cho * 588 +
                    jung * 28 +
                    jong


        return code.toChar()
    }


    /*
     * ============================================================
     * COMPOSE
     * ============================================================
     */
    fun compose(
        currentText: String,
        keyStr: String
    ): String {

        if (
            keyStr.isEmpty()
        ) {

            return currentText
        }


        var result =
            currentText


        keyStr.forEach { input ->

            result =
                composeSingle(
                    result,
                    input
                )
        }


        return result
    }


    private fun composeSingle(
        currentText: String,
        inputChar: Char
    ): String {

        if (
            currentText.isEmpty()
        ) {

            return inputChar.toString()
        }


        val inputIsConsonant =
            CHOSEONG.contains(
                inputChar
            )


        val inputIsVowel =
            JUNGSEONG.contains(
                inputChar
            )


        /*
         * Número, espacio, puntuación,
         * punto 천지인 temporal, etc.
         */
        if (
            !inputIsConsonant &&
            !inputIsVowel
        ) {

            return currentText +
                    inputChar
        }


        val lastChar =
            currentText.last()


        val prefix =
            currentText.dropLast(1)


        /*
         * ========================================================
         * CONSONANTE SUELTA + VOCAL
         *
         * ㄱ + ㅏ = 가
         * ========================================================
         */
        val standaloneCho =
            CHOSEONG.indexOf(
                lastChar
            )


        if (
            standaloneCho >= 0
        ) {

            if (
                inputIsVowel
            ) {

                val jung =
                    JUNGSEONG.indexOf(
                        inputChar
                    )


                return prefix +
                        createSyllable(
                            cho =
                                standaloneCho,

                            jung =
                                jung
                        )
            }


            /*
             * Otra consonante:
             *
             * se mantiene separada.
             *
             * Las dobles ㄲ ㄸ etc. ya vienen
             * directamente del teclado.
             */
            return currentText +
                    inputChar
        }


        /*
         * ========================================================
         * VOCAL SUELTA
         * ========================================================
         */
        val standaloneJung =
            JUNGSEONG.indexOf(
                lastChar
            )


        if (
            standaloneJung >= 0
        ) {

            if (
                inputIsVowel
            ) {

                val compound =
                    COMPOUND_VOWELS[
                        Pair(
                            lastChar,
                            inputChar
                        )
                    ]


                if (
                    compound != null
                ) {

                    return prefix +
                            compound
                }
            }


            return currentText +
                    inputChar
        }


        /*
         * ========================================================
         * SÍLABA COMPLETA
         * ========================================================
         */
        val code =
            lastChar.code


        if (
            code !in
            HANGUL_BASE..HANGUL_END
        ) {

            return currentText +
                    inputChar
        }


        val offset =
            code -
                    HANGUL_BASE


        val cho =
            offset /
                    588


        val jung =
            (
                    offset %
                            588
                    ) / 28


        val jong =
            offset %
                    28


        /*
         * ========================================================
         * SIN BATCHIM
         * ========================================================
         */
        if (
            jong == 0
        ) {

            /*
             * Intentar combinar vocal.
             *
             * 고 + ㅏ = 과
             */
            if (
                inputIsVowel
            ) {

                val currentVowel =
                    JUNGSEONG[
                        jung
                    ]


                val combined =
                    COMPOUND_VOWELS[
                        Pair(
                            currentVowel,
                            inputChar
                        )
                    ]


                if (
                    combined != null
                ) {

                    val newJung =
                        JUNGSEONG.indexOf(
                            combined
                        )


                    return prefix +
                            createSyllable(
                                cho =
                                    cho,

                                jung =
                                    newJung
                            )
                }


                return currentText +
                        inputChar
            }


            /*
             * Añadir consonante final.
             *
             * 가 + ㄴ = 간
             */
            if (
                inputIsConsonant
            ) {

                val newJong =
                    JONGSEONG.indexOf(
                        inputChar
                    )


                if (
                    newJong > 0
                ) {

                    return prefix +
                            createSyllable(
                                cho =
                                    cho,

                                jung =
                                    jung,

                                jong =
                                    newJong
                            )
                }


                /*
                 * ㄸ, ㅃ, ㅉ no son
                 * finales válidos.
                 */
                return currentText +
                        inputChar
            }
        }


        /*
         * ========================================================
         * CON BATCHIM
         * ========================================================
         */
        if (
            jong > 0
        ) {

            val currentFinal =
                JONGSEONG[
                    jong
                ]


            /*
             * ----------------------------------------------------
             * FINAL + VOCAL
             * ----------------------------------------------------
             */
            if (
                inputIsVowel
            ) {

                val nextJung =
                    JUNGSEONG.indexOf(
                        inputChar
                    )


                /*
                 * Batchim compuesto.
                 *
                 * Primera consonante queda atrás.
                 * Segunda pasa a la nueva sílaba.
                 */
                val pair =
                    COMPOUND_FINALS_REVERSE[
                        currentFinal
                    ]


                if (
                    pair != null
                ) {

                    val firstFinal =
                        JONGSEONG.indexOf(
                            pair.first
                        )


                    val secondCho =
                        CHOSEONG.indexOf(
                            pair.second
                        )


                    if (
                        firstFinal > 0 &&
                        secondCho >= 0
                    ) {

                        val previous =
                            createSyllable(
                                cho =
                                    cho,

                                jung =
                                    jung,

                                jong =
                                    firstFinal
                            )


                        val next =
                            createSyllable(
                                cho =
                                    secondCho,

                                jung =
                                    nextJung
                            )


                        return prefix +
                                previous +
                                next
                    }
                }


                /*
                 * Batchim simple.
                 *
                 * 간 + ㅏ
                 * ↓
                 * 가나
                 */
                val nextCho =
                    CHOSEONG.indexOf(
                        currentFinal
                    )


                if (
                    nextCho >= 0
                ) {

                    val previous =
                        createSyllable(
                            cho =
                                cho,

                            jung =
                                jung,

                            jong =
                                0
                        )


                    val next =
                        createSyllable(
                            cho =
                                nextCho,

                            jung =
                                nextJung
                        )


                    return prefix +
                            previous +
                            next
                }


                return currentText +
                        inputChar
            }


            /*
             * ----------------------------------------------------
             * FINAL + CONSONANTE
             *
             * 갑 + ㅅ = 값
             * ----------------------------------------------------
             */
            if (
                inputIsConsonant
            ) {

                val compound =
                    COMPOUND_FINALS[
                        Pair(
                            currentFinal,
                            inputChar
                        )
                    ]


                if (
                    compound != null
                ) {

                    val newJong =
                        JONGSEONG.indexOf(
                            compound
                        )


                    if (
                        newJong > 0
                    ) {

                        return prefix +
                                createSyllable(
                                    cho =
                                        cho,

                                    jung =
                                        jung,

                                    jong =
                                        newJong
                                )
                    }
                }


                /*
                 * No forman batchim compuesto.
                 */
                return currentText +
                        inputChar
            }
        }


        return currentText +
                inputChar
    }


    /*
     * ============================================================
     * BACKSPACE
     * ============================================================
     */
    fun backspace(
        currentText: String
    ): String {

        if (
            currentText.isEmpty()
        ) {

            return ""
        }


        val lastChar =
            currentText.last()


        val prefix =
            currentText.dropLast(1)


        val code =
            lastChar.code


        /*
         * No es una sílaba Hangul.
         */
        if (
            code !in
            HANGUL_BASE..HANGUL_END
        ) {

            val standaloneCompound =
                COMPOUND_VOWELS_REVERSE[
                    lastChar
                ]


            if (
                standaloneCompound != null
            ) {

                return prefix +
                        standaloneCompound.first
            }


            return prefix
        }


        val offset =
            code -
                    HANGUL_BASE


        val cho =
            offset /
                    588


        val jung =
            (
                    offset %
                            588
                    ) / 28


        val jong =
            offset %
                    28


        /*
         * Tiene batchim.
         */
        if (
            jong > 0
        ) {

            val final =
                JONGSEONG[
                    jong
                ]


            val compound =
                COMPOUND_FINALS_REVERSE[
                    final
                ]


            /*
             * 값 -> 갑
             */
            if (
                compound != null
            ) {

                val previousFinal =
                    JONGSEONG.indexOf(
                        compound.first
                    )


                return prefix +
                        createSyllable(
                            cho =
                                cho,

                            jung =
                                jung,

                            jong =
                                previousFinal
                        )
            }


            /*
             * 간 -> 가
             */
            return prefix +
                    createSyllable(
                        cho =
                            cho,

                        jung =
                            jung,

                        jong =
                            0
                    )
        }


        /*
         * Sin batchim.
         *
         * Revisar vocal compuesta.
         */
        val vowel =
            JUNGSEONG[
                jung
            ]


        val compoundVowel =
            COMPOUND_VOWELS_REVERSE[
                vowel
            ]


        /*
         * 과 -> 고
         */
        if (
            compoundVowel != null
        ) {

            val previousVowel =
                JUNGSEONG.indexOf(
                    compoundVowel.first
                )


            return prefix +
                    createSyllable(
                        cho =
                            cho,

                        jung =
                            previousVowel
                    )
        }


        /*
         * 가 -> ㄱ
         */
        return prefix +
                CHOSEONG[
                    cho
                ]
    }


    /*
     * ============================================================
     * DESCOMPONER PARA VALIDACIÓN
     * ============================================================
     */
    fun decomposeToJamos(
        text: String
    ): List<Char> {

        val result =
            mutableListOf<Char>()


        text.forEach { char ->

            val code =
                char.code


            if (
                code in
                HANGUL_BASE..HANGUL_END
            ) {

                val offset =
                    code -
                            HANGUL_BASE


                val cho =
                    offset /
                            588


                val jung =
                    (
                            offset %
                                    588
                            ) / 28


                val jong =
                    offset %
                            28


                result.add(
                    CHOSEONG[
                        cho
                    ]
                )


                val vowel =
                    JUNGSEONG[
                        jung
                    ]


                val compoundVowel =
                    COMPOUND_VOWELS_REVERSE[
                        vowel
                    ]


                if (
                    compoundVowel != null
                ) {

                    result.add(
                        compoundVowel.first
                    )

                    result.add(
                        compoundVowel.second
                    )

                } else {

                    result.add(
                        vowel
                    )
                }


                if (
                    jong > 0
                ) {

                    val final =
                        JONGSEONG[
                            jong
                        ]


                    val compoundFinal =
                        COMPOUND_FINALS_REVERSE[
                            final
                        ]


                    if (
                        compoundFinal != null
                    ) {

                        result.add(
                            compoundFinal.first
                        )

                        result.add(
                            compoundFinal.second
                        )

                    } else {

                        result.add(
                            final
                        )
                    }
                }


            } else {

                result.add(
                    char
                )
            }
        }


        return result
    }


    /*
     * ============================================================
     * VALIDACIÓN EN TIEMPO REAL
     * ============================================================
     */
    fun getCharacterValidation(
        typedText: String,
        targetText: String
    ): BooleanArray {

        val validation =
            BooleanArray(
                typedText.length
            )


        if (
            typedText.isEmpty() ||
            targetText.isEmpty()
        ) {

            return validation
        }


        val targetJamos =
            decomposeToJamos(
                targetText
            )


        var targetOffset =
            0


        var errorFound =
            false


        for (
        index in
        typedText.indices
        ) {

            if (
                errorFound
            ) {

                validation[
                    index
                ] = false

                continue
            }


            val typedJamos =
                decomposeToJamos(
                    typedText[
                        index
                    ].toString()
                )


            if (
                targetOffset +
                typedJamos.size >
                targetJamos.size
            ) {

                validation[
                    index
                ] = false

                errorFound =
                    true

                continue
            }


            val expected =
                targetJamos.subList(
                    targetOffset,

                    targetOffset +
                            typedJamos.size
                )


            if (
                typedJamos ==
                expected
            ) {

                validation[
                    index
                ] = true


                targetOffset +=
                    typedJamos.size

            } else {

                validation[
                    index
                ] = false


                errorFound =
                    true
            }
        }


        return validation
    }
}