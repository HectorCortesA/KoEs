package com.hector.koes.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hector.koes.model.DictionaryItem
import com.hector.koes.components.card.CardDictionary
import com.hector.koes.components.card.ModalDictionary
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.ui.theme.Background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiccionaryView(
    onNavigate: (String) -> Unit = {},
    placeholder: String = "Busca la palabra"
) {
    var value by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    
    // Estado para el Modal
    var showModal by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<DictionaryItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // Lista de ejemplo para simular la API
    val dictionaryItems = listOf(
        DictionaryItem("Hola", "안녕하세요", "annyeonghaseyo", "an-nyeong-ha-se-yo"),
        DictionaryItem("Gracias", "감사합니다", "gamsahamnida", "gam-sa-ham-ni-da"),
        DictionaryItem("Si", "네", "ne", "ne"),
        DictionaryItem("No", "아니요", "aniyo", "a-ni-yo"),
        DictionaryItem("Adiós", "안녕", "annyeong", "an-nyeong")
    )

    // Filtrado básico
    val filteredItems = dictionaryItems.filter {
        it.spanish.contains(value, ignoreCase = true) || 
        it.korean.contains(value, ignoreCase = true)
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
                .padding(top = 60.dp),
            onNavigate = onNavigate
        )

        BasicTextField(
            value = value,
            onValueChange = { value = it },
            singleLine = true,
            cursorBrush = SolidColor(Color.Black),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 150.dp)
                .width(362.dp)
                .height(43.dp)
                .background(
                    color = Color(0x33D9D9D9),
                    shape = RoundedCornerShape(size = 10.dp)
                )
                // Efecto Glass: Borde con degradado blanco
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(size = 10.dp)
                )
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Black.copy(alpha = 0.4f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 240.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            filteredItems.forEach { item ->
                CardDictionary(
                    wordSpanish = item.spanish,
                    wordCorea = item.korean,
                    pronunciation = item.romanization,
                    onClick = {
                        selectedItem = item
                        showModal = true
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            
            // Espacio extra al final para el scroll
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Implementación del ModalBottomSheet
        if (showModal && selectedItem != null) {
            ModalBottomSheet(
                onDismissRequest = { showModal = false },
                sheetState = sheetState,
                containerColor = Color(0x80FFFFFF),
                dragHandle = null,
                tonalElevation = 0.dp,
                scrimColor = Color.Black.copy(alpha = 0.32f),
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
            ) {
                // El contenido del modal
                Box(modifier = Modifier.fillMaxWidth().background(Color(0x80FFFFFF))) {
                    ModalDictionary(
                        wordSpanish = selectedItem!!.spanish,
                        wordCorea = selectedItem!!.korean,
                        romanization = selectedItem!!.romanization,
                        pronunciation = selectedItem!!.pronunciation,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiccionaryViewPreview() {
    DiccionaryView()
}
