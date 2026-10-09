package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.text.TextRange
import com.hector.koes.components.KoreanKeyboard.KoreanKeyboard
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.components.loading.BouncingDotsLoadingAnimation
import com.hector.koes.ui.theme.Background
import com.hector.koes.util.HangulUtils
import com.hector.koes.viewModel.HomeViewModel
import com.hector.koes.viewModel.WritingMode

@Composable
fun Home(
    onNavigate: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    val currentItem by viewModel.currentItem.collectAsState()
    val writingMode by viewModel.writingMode.collectAsState()
    
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var showTooltip by remember { mutableStateOf(false) }
    var showCustomKeyboard by remember { mutableStateOf(true) }

    // Obtenemos la palabra objetivo según el modo
    val suggestion = if (writingMode == WritingMode.PALABRAS) {
        currentItem?.wordCoreano ?: ""
    } else {
        currentItem?.ejemploKoreano ?: ""
    }

    // Obtenemos el texto en español para mostrar
    val displaySpanish = if (writingMode == WritingMode.PALABRAS) {
        currentItem?.wordSpanish
    } else {
        currentItem?.ejemploSpanish
    }

    // Limpiamos el texto cuando cambia la palabra
    LaunchedEffect(currentItem) {
        textFieldValue = TextFieldValue("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Navbar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 10.dp),
            onNavigate = onNavigate
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(60.dp)
            ) {
                if (displaySpanish != null) {
                    Text(
                        text = displaySpanish,
                        color = Color.Black,
                        fontSize = 18.sp,
                        modifier = if (showTooltip && writingMode == WritingMode.PALABRAS) {
                            Modifier.blur(5.dp)
                        } else {
                            Modifier
                        }
                    )
                } else {
                    BouncingDotsLoadingAnimation(
                        dotSize = 10.dp,
                        dotColorActive = Color(0xFF75A5E3),
                        dotColorInactive = Color.White
                    )
                }

                if (showTooltip && writingMode == WritingMode.PALABRAS) {
                    Box(
                        modifier = Modifier
                            .width(162.dp)
                            .height(39.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Color.White.copy(alpha = 0.50f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentItem?.romanization ?: "",
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            Text(
                text = suggestion,
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                showTooltip = true
                                tryAwaitRelease()
                                showTooltip = false
                            }
                        )
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                val visualText = remember(textFieldValue.text, suggestion) {
                    buildAnnotatedString {
                        val text = textFieldValue.text
                        val isWordFullyComplete = text.isNotEmpty() &&
                                suggestion.isNotEmpty() &&
                                (text.trim() == suggestion.trim() || HangulUtils.decomposeToJamos(text.trim()) == HangulUtils.decomposeToJamos(suggestion.trim()))

                        val validation = HangulUtils.getCharacterValidation(text, suggestion)

                        for (i in text.indices) {
                            val isCorrect = isWordFullyComplete || validation.getOrElse(i) { false }
                            withStyle(style = SpanStyle(color = if (isCorrect) Color.Black else Color.Red)) {
                                append(text[i])
                            }
                        }
                        if (text.length < suggestion.length && !isWordFullyComplete) {
                            withStyle(style = SpanStyle(color = Color.Black.copy(alpha = 0.15f))) {
                                append(suggestion.substring(text.length))
                            }
                        }
                    }
                }

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        if (!showCustomKeyboard) {
                            textFieldValue = newValue
                            viewModel.checkCompletion(newValue.text) {
                                textFieldValue = TextFieldValue("")
                            }
                        }
                    },
                    readOnly = showCustomKeyboard,
                    textStyle = TextStyle(
                        color = Color.Transparent,
                        textAlign = TextAlign.Center,
                        fontSize = if (writingMode == WritingMode.PALABRAS) 40.sp else 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    showCustomKeyboard = true
                                }
                            )
                        },
                    cursorBrush = SolidColor(Color.Black),
                    singleLine = writingMode == WritingMode.PALABRAS,
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = visualText,
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = if (writingMode == WritingMode.PALABRAS) 40.sp else 24.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            innerTextField()
                        }
                    }
                )
            }
        }

        if (showCustomKeyboard) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                KoreanKeyboard(
                    onKeyClick = { key ->
                        val newText = if (key == " ") {
                            textFieldValue.text + " "
                        } else {
                            HangulUtils.compose(textFieldValue.text, key)
                        }
                        textFieldValue = TextFieldValue(newText, selection = TextRange(newText.length))
                        viewModel.checkCompletion(newText) {
                            textFieldValue = TextFieldValue("")
                        }
                    },
                    onShiftClick = {},
                    onDeleteClick = {
                        val newText = HangulUtils.backspace(textFieldValue.text)
                        textFieldValue = TextFieldValue(newText, selection = TextRange(newText.length))
                        viewModel.checkCompletion(newText) {}
                    },
                    onNumberClick = {},
                    onSettingsClick = {},
                    onNativeKeyboardClick = {
                        showCustomKeyboard = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Home()
}
