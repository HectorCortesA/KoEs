package com.hector.koes.components.KoreanKeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource


private val KeyboardBackground =
    Color(0xFFA4B6C2)

private val SpecialKeyBackground =
    Color(0xFFE3E9EC)

private val NormalKeyBackground =
    Color.White.copy(alpha = 0.80f)


/*
 * Tiempo para recorrer una tecla agrupada.
 *
 * ㄱㅋ:
 *
 * 1 toque -> ㄱ
 * 2 toques rápidos -> ㅋ
 * 3 toques rápidos -> ㄲ
 *
 * Si pasa este tiempo, vuelve a comenzar
 * como una nueva consonante.
 */
private val MULTI_TAP_TIMEOUT =
    850.milliseconds


private enum class KeyboardMode {
    NORMAL,
    TEN_KEY
}


/*
 * ============================================================
 * SECUENCIAS DEL TECLADO 천지인
 * ============================================================
 *
 * Las teclas visibles son:
 *
 * ㅣ   ㆍ   ㅡ
 *
 * y mediante secuencias se generan las 21 vocales modernas.
 */
private val TEN_KEY_VOWELS = mapOf(

    // Básicas / estados iniciales
    "ㅣ" to "ㅣ",
    "ㆍ" to "ㆍ",
    "ㅡ" to "ㅡ",

    // ㅏ ㅑ
    "ㅣㆍ" to "ㅏ",
    "ㅣㆍㆍ" to "ㅑ",

    // ㅓ ㅕ
    "ㆍㅣ" to "ㅓ",
    "ㆍㆍㅣ" to "ㅕ",

    // ㅗ ㅛ
    "ㆍㅡ" to "ㅗ",
    "ㆍㆍㅡ" to "ㅛ",

    // ㅜ ㅠ
    "ㅡㆍ" to "ㅜ",
    "ㅡㆍㆍ" to "ㅠ",

    // ㅐ ㅒ
    "ㅣㆍㅣ" to "ㅐ",
    "ㅣㆍㆍㅣ" to "ㅒ",

    // ㅔ ㅖ
    "ㆍㅣㅣ" to "ㅔ",
    "ㆍㆍㅣㅣ" to "ㅖ",

    // ㅢ
    "ㅡㅣ" to "ㅢ",

    // ㅚ
    "ㆍㅡㅣ" to "ㅚ",

    // ㅟ
    "ㅡㆍㅣ" to "ㅟ",

    // ㅘ
    "ㆍㅡㅣㆍ" to "ㅘ",

    // ㅙ
    "ㆍㅡㅣㆍㅣ" to "ㅙ",

    // ㅝ
    "ㅡㆍㆍㅣ" to "ㅝ",

    // ㅞ
    "ㅡㆍㆍㅣㅣ" to "ㅞ"
)


/*
 * Todas las secuencias parciales válidas.
 *
 * Ejemplo:
 *
 * ㆍ
 * ㆍㆍ
 * ㆍㆍㅣ
 *
 * Esto permite saber si todavía estamos
 * construyendo una vocal.
 */
private val TEN_KEY_VOWEL_PREFIXES: Set<String> =
    buildSet {

        TEN_KEY_VOWELS.keys.forEach { sequence ->

            for (index in 1..sequence.length) {

                add(
                    sequence.take(index)
                )
            }
        }
    }


/*
 * Estas vocales necesitan dos pasos de backspace
 * en HangulUtils para desaparecer completamente:
 *
 * 과 -> 고 -> ㄱ
 */
private val COMPOUND_MEDIALS =
    setOf(
        "ㅘ",
        "ㅙ",
        "ㅚ",
        "ㅝ",
        "ㅞ",
        "ㅟ",
        "ㅢ"
    )


@Composable
fun KoreanKeyboard(
    onKeyClick: (String) -> Unit,

    onShiftClick: () -> Unit = {},

    onDeleteClick: () -> Unit = {},

    onNumberClick: () -> Unit = {},

    onSettingsClick: () -> Unit = {},

    onNativeKeyboardClick: () -> Unit = {}
) {

    var keyboardMode by remember {
        mutableStateOf(
            KeyboardMode.NORMAL
        )
    }


    when (keyboardMode) {

        KeyboardMode.NORMAL -> {

            NormalKoreanKeyboard(
                onKeyClick = onKeyClick,

                onShiftClick =
                    onShiftClick,

                onDeleteClick =
                    onDeleteClick,

                onNumberClick =
                    onNumberClick,

                onSettingsClick = {

                    keyboardMode =
                        KeyboardMode.TEN_KEY

                    onSettingsClick()
                },

                onNativeKeyboardClick =
                    onNativeKeyboardClick
            )
        }


        KeyboardMode.TEN_KEY -> {

            TenKeyKoreanKeyboard(
                onKeyClick =
                    onKeyClick,

                onDeleteClick =
                    onDeleteClick,

                onNumberClick =
                    onNumberClick,

                onBackToNormalKeyboard = {

                    keyboardMode =
                        KeyboardMode.NORMAL
                }
            )
        }
    }
}


/*
 * ============================================================
 * TECLADO COREANO NORMAL
 * ============================================================
 */

@Composable
private fun NormalKoreanKeyboard(
    onKeyClick: (String) -> Unit,

    onShiftClick: () -> Unit,

    onDeleteClick: () -> Unit,

    onNumberClick: () -> Unit,

    onSettingsClick: () -> Unit,

    onNativeKeyboardClick: () -> Unit
) {

    var shiftEnabled by remember {
        mutableStateOf(false)
    }


    val row1 = listOf(
        "ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ",
        "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"
    )


    val row2 = listOf(
        "ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ",
        "ㅗ", "ㅓ", "ㅏ", "ㅣ"
    )


    val row3 = listOf(
        "ㅋ", "ㅌ", "ㅊ", "ㅍ",
        "ㅠ", "ㅜ", "ㅡ"
    )


    val shiftCharacters = mapOf(
        "ㅂ" to "ㅃ",
        "ㅈ" to "ㅉ",
        "ㄷ" to "ㄸ",
        "ㄱ" to "ㄲ",
        "ㅅ" to "ㅆ",
        "ㅐ" to "ㅒ",
        "ㅔ" to "ㅖ"
    )


    fun displayCharacter(
        character: String
    ): String {

        if (!shiftEnabled) {
            return character
        }

        return shiftCharacters[character]
            ?: character
    }


    fun sendCharacter(
        character: String
    ) {

        val result =
            displayCharacter(
                character
            )

        onKeyClick(result)

        if (shiftEnabled) {
            shiftEnabled = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(289.dp)
            .background(
                color =
                    KeyboardBackground,

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // FILA 1
        Row(
            modifier =
                Modifier.width(390.dp),

            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            row1.forEach { letter ->

                KoreanKey(
                    letter =
                        displayCharacter(letter),

                    onClick = {

                        sendCharacter(letter)
                    }
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(11.dp)
        )


        // FILA 2
        Row(
            modifier =
                Modifier.width(345.dp),

            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            row2.forEach { letter ->

                KoreanKey(
                    letter =
                        displayCharacter(letter),

                    onClick = {

                        sendCharacter(letter)
                    }
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(11.dp)
        )


        // FILA 3
        Row(
            modifier =
                Modifier.width(396.dp),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            SpecialTextKey(
                width = 39.dp,
                height = 42.dp,
                text = "⇧",
                fontSize = 24.sp,
                selected = shiftEnabled,

                onClick = {

                    shiftEnabled =
                        !shiftEnabled

                    onShiftClick()
                }
            )


            Row(
                modifier =
                    Modifier.width(300.dp),

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                row3.forEach { letter ->

                    KoreanKey(
                        letter =
                            displayCharacter(letter),

                        onClick = {

                            sendCharacter(letter)
                        }
                    )
                }
            }


            SpecialTextKey(
                width = 39.dp,
                height = 42.dp,
                text = "⌫",
                fontSize = 22.sp,
                onClick = onDeleteClick
            )
        }


        Spacer(
            modifier =
                Modifier.height(11.dp)
        )


        // FILA INFERIOR
        Row(
            modifier =
                Modifier.width(394.dp),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                SpecialTextKey(
                    width = 55.dp,
                    height = 42.dp,
                    text = "123",
                    onClick = onNumberClick
                )


                Spacer(
                    modifier =
                        Modifier.width(3.dp)
                )


                SpecialTextKey(
                    width = 42.dp,
                    height = 42.dp,
                    text = "⚙",
                    fontSize = 21.sp,
                    onClick = onSettingsClick
                )
            }


            SpecialTextKey(
                width = 165.dp,
                height = 42.dp,
                text = "space",

                onClick = {

                    onKeyClick(" ")
                }
            )


            SpecialTextKey(
                width = 42.dp,
                height = 42.dp,
                text = "⌨",
                fontSize = 20.sp,
                onClick = onNativeKeyboardClick
            )
        }
    }
}


/*
 * ============================================================
 * TECLADO COREANO 10-KEY / 천지인
 * ============================================================
 */

@Composable
private fun TenKeyKoreanKeyboard(
    onKeyClick: (String) -> Unit,

    onDeleteClick: () -> Unit,

    onNumberClick: () -> Unit,

    onBackToNormalKeyboard: () -> Unit
) {

    /*
     * --------------------------------------------------------
     * ESTADO DE CONSONANTES MULTI-TAP
     * --------------------------------------------------------
     */

    var lastCycleGroup by remember {
        mutableStateOf<String?>(null)
    }

    var cycleIndex by remember {
        mutableStateOf(0)
    }

    var lastCyclePress by remember {
        mutableStateOf<TimeMark?>(null)
    }


    /*
     * --------------------------------------------------------
     * ESTADO DE VOCALES 천지인
     * --------------------------------------------------------
     *
     * Aquí guardamos las teclas físicas:
     *
     * ㅡㆍ
     *
     * no solamente el resultado:
     *
     * ㅜ
     */
    var vowelSequence by remember {
        mutableStateOf("")
    }


    /*
     * Cuántos pasos de backspace hacen falta
     * para quitar el resultado actual.
     */
    var vowelDeleteSteps by remember {
        mutableStateOf(0)
    }


    /*
     * Grupos tradicionales.
     *
     * La etiqueta del botón NO cambia.
     */
    val consonantGroups =
        mapOf(

            "C1" to listOf(
                "ㄱ",
                "ㅋ",
                "ㄲ"
            ),

            "C2" to listOf(
                "ㄴ",
                "ㄹ"
            ),

            "C3" to listOf(
                "ㄷ",
                "ㅌ",
                "ㄸ"
            ),

            "C4" to listOf(
                "ㅂ",
                "ㅍ",
                "ㅃ"
            ),

            "C5" to listOf(
                "ㅅ",
                "ㅎ",
                "ㅆ"
            ),

            "C6" to listOf(
                "ㅈ",
                "ㅊ",
                "ㅉ"
            ),

            "C7" to listOf(
                "ㅇ",
                "ㅁ"
            ),

            "P1" to listOf(
                ".",
                ",",
                "?",
                "!"
            )
        )


    fun resetCycle() {

        lastCycleGroup =
            null

        cycleIndex =
            0

        lastCyclePress =
            null
    }


    fun resetVowel() {

        vowelSequence =
            ""

        vowelDeleteSteps =
            0
    }


    /*
     * Elimina SOLO la vocal que actualmente
     * pertenece a la secuencia 천지인.
     */
    fun clearCurrentVowelOutput() {

        repeat(
            vowelDeleteSteps
        ) {

            onDeleteClick()
        }

        vowelDeleteSteps =
            0
    }


    /*
     * Número de pasos necesarios para retirar
     * completamente una vocal del HangulUtils.
     */
    fun deleteDepthFor(
        result: String
    ): Int {

        return if (
            result in COMPOUND_MEDIALS
        ) {

            2

        } else {

            1
        }
    }


    /*
     * Renderiza el estado actual de la secuencia.
     *
     * Ejemplo:
     *
     * ㅡ
     * ↓
     * ㅡㆍ
     * ↓
     * ㅜ
     */
    fun renderVowelSequence(
        sequence: String
    ) {

        val completedVowel =
            TEN_KEY_VOWELS[
                sequence
            ]


        if (completedVowel != null) {

            onKeyClick(
                completedVowel
            )

            vowelDeleteSteps =
                deleteDepthFor(
                    completedVowel
                )

            return
        }


        /*
         * Actualmente el caso importante es:
         *
         * ㆍㆍ
         *
         * Todavía no es una vocal terminada,
         * porque puede convertirse en:
         *
         * ㆍㆍㅣ = ㅕ
         * ㆍㆍㅡ = ㅛ
         */
        sequence.forEach { character ->

            onKeyClick(
                character.toString()
            )
        }


        vowelDeleteSteps =
            sequence.length
    }


    /*
     * --------------------------------------------------------
     * PRESIONAR ㅣ / ㆍ / ㅡ
     * --------------------------------------------------------
     */
    fun pressVowelKey(
        key: String
    ) {

        /*
         * Al entrar en una vocal dejamos de recorrer
         * la consonante anterior.
         */
        resetCycle()


        val candidate =
            vowelSequence +
                    key


        /*
         * Si la combinación todavía pertenece al
         * árbol de vocales 천지인...
         */
        if (
            candidate in
            TEN_KEY_VOWEL_PREFIXES
        ) {

            /*
             * Quitamos el resultado anterior.
             */
            clearCurrentVowelOutput()


            /*
             * Guardamos las teclas físicas.
             */
            vowelSequence =
                candidate


            /*
             * Escribimos su nuevo resultado.
             */
            renderVowelSequence(
                vowelSequence
            )


            return
        }


        /*
         * La combinación anterior terminó.
         *
         * Comenzamos una nueva vocal con la
         * tecla recién presionada.
         */
        resetVowel()


        vowelSequence =
            key


        renderVowelSequence(
            vowelSequence
        )
    }


    /*
     * --------------------------------------------------------
     * CONSONANTES AGRUPADAS
     * --------------------------------------------------------
     *
     * La etiqueta nunca cambia.
     *
     * ㄱㅋ:
     *
     * toque 1 = ㄱ
     * toque 2 = ㅋ
     * toque 3 = ㄲ
     */
    fun pressCycleKey(
        groupId: String
    ) {

        /*
         * La vocal ya terminó.
         */
        resetVowel()


        val characters =
            consonantGroups[
                groupId
            ] ?: return


        val elapsed =
            lastCyclePress
                ?.elapsedNow()


        val continueCycle =
            lastCycleGroup ==
                    groupId &&
                    elapsed != null &&
                    elapsed <=
                    MULTI_TAP_TIMEOUT


        if (continueCycle) {

            cycleIndex =
                (
                        cycleIndex +
                                1
                        ) %
                        characters.size


            /*
             * Borramos la opción anterior.
             *
             * Ejemplo:
             *
             * 각
             * ↓ borrar ㄱ final
             * 가
             * ↓ agregar ㅋ
             * 갘
             */
            onDeleteClick()


            onKeyClick(
                characters[
                    cycleIndex
                ]
            )

        } else {

            cycleIndex =
                0


            onKeyClick(
                characters[
                    0
                ]
            )
        }


        lastCycleGroup =
            groupId


        lastCyclePress =
            TimeSource
                .Monotonic
                .markNow()
    }


    /*
     * --------------------------------------------------------
     * BACKSPACE DEL 10-KEY
     * --------------------------------------------------------
     *
     * Si estamos construyendo una vocal:
     *
     * ㅡㆍ
     * ㅜ
     *
     * Backspace:
     *
     * ㅡ
     */
    fun tenKeyBackspace() {

        resetCycle()


        if (
            vowelSequence.isNotEmpty()
        ) {

            clearCurrentVowelOutput()


            val previousSequence =
                vowelSequence
                    .dropLast(1)


            vowelSequence =
                previousSequence


            if (
                previousSequence.isNotEmpty()
            ) {

                renderVowelSequence(
                    previousSequence
                )
            }


            return
        }


        onDeleteClick()
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(289.dp)
            .background(
                color =
                    KeyboardBackground,

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(15.dp)
        )


        Row(
            modifier =
                Modifier.width(394.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.Top
        ) {

            /*
             * ==================================================
             * IZQUIERDA
             * ==================================================
             */
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(
                        7.dp
                    )
            ) {

                TenKeySpecialKey(
                    width = 67.dp,
                    height = 48.dp,
                    text = "#123",

                    onClick = {

                        resetCycle()
                        resetVowel()

                        onNumberClick()
                    }
                )


                TenKeySpecialKey(
                    width = 67.dp,
                    height = 48.dp,
                    text = "한글",

                    onClick = {

                        resetCycle()
                        resetVowel()

                        onBackToNormalKeyboard()
                    }
                )
            }


            /*
             * ==================================================
             * CENTRO
             * ==================================================
             */
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(
                        7.dp
                    )
            ) {

                /*
                 * ----------------------------------------------
                 * VOCALES
                 *
                 * TEXTO SIEMPRE FIJO
                 * ----------------------------------------------
                 */
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            7.dp
                        )
                ) {

                    TenKey(
                        text = "ㅣ",

                        onClick = {

                            pressVowelKey(
                                "ㅣ"
                            )
                        }
                    )


                    TenKey(
                        text = "ㆍ",

                        onClick = {

                            pressVowelKey(
                                "ㆍ"
                            )
                        }
                    )


                    TenKey(
                        text = "ㅡ",

                        onClick = {

                            pressVowelKey(
                                "ㅡ"
                            )
                        }
                    )
                }


                /*
                 * ----------------------------------------------
                 * FILA 2
                 * ----------------------------------------------
                 */
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            7.dp
                        )
                ) {

                    TenKey(
                        text = "ㄱㅋ",

                        onClick = {

                            pressCycleKey(
                                "C1"
                            )
                        }
                    )


                    TenKey(
                        text = "ㄴㄹ",

                        onClick = {

                            pressCycleKey(
                                "C2"
                            )
                        }
                    )


                    TenKey(
                        text = "ㄷㅌ",

                        onClick = {

                            pressCycleKey(
                                "C3"
                            )
                        }
                    )
                }


                /*
                 * ----------------------------------------------
                 * FILA 3
                 * ----------------------------------------------
                 */
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            7.dp
                        )
                ) {

                    TenKey(
                        text = "ㅂㅍ",

                        onClick = {

                            pressCycleKey(
                                "C4"
                            )
                        }
                    )


                    TenKey(
                        text = "ㅅㅎ",

                        onClick = {

                            pressCycleKey(
                                "C5"
                            )
                        }
                    )


                    TenKey(
                        text = "ㅈㅊ",

                        onClick = {

                            pressCycleKey(
                                "C6"
                            )
                        }
                    )
                }


                /*
                 * ----------------------------------------------
                 * FILA 4
                 * ----------------------------------------------
                 */
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            7.dp
                        )
                ) {

                    TenKey(
                        text = "ㅇㅁ",

                        onClick = {

                            pressCycleKey(
                                "C7"
                            )
                        }
                    )


                    TenKey(
                        text = ".,?!",
                        fontSize = 16.sp,

                        onClick = {

                            pressCycleKey(
                                "P1"
                            )
                        }
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                72.dp
                            )
                    )
                }
            }


            /*
             * ==================================================
             * DERECHA
             * ==================================================
             */
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(
                        7.dp
                    )
            ) {

                TenKeySpecialKey(
                    width = 62.dp,
                    height = 48.dp,
                    text = "⌫",
                    fontSize = 23.sp,

                    onClick = {

                        tenKeyBackspace()
                    }
                )


                TenKeySpecialKey(
                    width = 62.dp,
                    height = 158.dp,
                    text = "스페이스",
                    fontSize = 11.sp,

                    onClick = {

                        resetCycle()
                        resetVowel()

                        onKeyClick(
                            " "
                        )
                    }
                )
            }
        }
    }
}


/*
 * ============================================================
 * TECLA NORMAL
 * ============================================================
 */

@Composable
fun KoreanKey(
    letter: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(35.dp)
            .height(42.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(
                color = NormalKeyBackground,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {

                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                letter,

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Medium,

            color =
                Color.Black
        )
    }
}


/*
 * ============================================================
 * TECLA 10-KEY
 * ============================================================
 */

@Composable
private fun TenKey(
    text: String,

    fontSize: TextUnit =
        20.sp,

    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(72.dp)
            .height(48.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(
                color =
                    NormalKeyBackground,

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            )
            .clickable {

                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        /*
         * IMPORTANTE:
         *
         * Este texto NO cambia.
         *
         * ㄱㅋ siempre muestra ㄱㅋ aunque
         * internamente esté enviando:
         *
         * ㄱ
         * ㅋ
         * ㄲ
         */
        Text(
            text =
                text,

            fontSize =
                fontSize,

            fontWeight =
                FontWeight.Medium,

            color =
                Color.Black
        )
    }
}


/*
 * ============================================================
 * TECLA ESPECIAL 10-KEY
 * ============================================================
 */

@Composable
private fun TenKeySpecialKey(
    width: Dp,

    height: Dp,

    text: String,

    fontSize: TextUnit =
        16.sp,

    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(
                color =
                    SpecialKeyBackground,

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            )
            .clickable {

                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                text,

            fontSize =
                fontSize,

            fontWeight =
                FontWeight.Medium,

            color =
                Color.Black
        )
    }
}


/*
 * ============================================================
 * TECLA ESPECIAL NORMAL
 * ============================================================
 */

@Composable
fun SpecialTextKey(
    width: Dp,

    height: Dp,

    text: String,

    fontSize: TextUnit =
        14.sp,

    selected: Boolean =
        false,

    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(
                color =
                    if (selected) {
                        Color.White
                    } else {
                        SpecialKeyBackground
                    },

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            )
            .clickable {

                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                text,

            fontSize =
                fontSize,

            fontWeight =
                FontWeight.Medium,

            color =
                Color.Black
        )
    }
}


/*
 * ============================================================
 * PREVIEW
 * ============================================================
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 402,
    heightDp = 320
)
@Composable
fun KoreanKeyboardPreview() {

    KoreanKeyboard(
        onKeyClick = { key ->

            println(
                "Tecla: $key"
            )
        },

        onDeleteClick = {

            println(
                "Borrar"
            )
        },

        onNumberClick = {

            println(
                "Números"
            )
        },

        onSettingsClick = {

            println(
                "Abrir teclado 10-key"
            )
        },

        onNativeKeyboardClick = {

            println(
                "Teclado nativo"
            )
        }
    )
}