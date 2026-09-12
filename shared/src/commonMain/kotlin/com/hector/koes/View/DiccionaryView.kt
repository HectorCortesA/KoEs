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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hector.koes.viewModel.DictionaryViewModel
import androidx.compose.runtime.collectAsState
import com.hector.koes.model.DictionaryItem
import com.hector.koes.components.card.CardDictionary
import com.hector.koes.components.card.ModalDictionary
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.ui.theme.Background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiccionaryView(
    onNavigate: (String) -> Unit = {},
    placeholder: String = "Busca la palabra",
    viewModel: DictionaryViewModel = viewModel { DictionaryViewModel() }
) {
    val words by viewModel.words.collectAsState()
    val query by viewModel.query.collectAsState()
    
    val listState = rememberLazyListState()
    
    // Detectamos cuando llegamos al final de la lista para cargar más
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            
            // Si estamos a 5 elementos del final, cargamos más
            lastVisibleItemIndex >= totalItemsCount - 5 && totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadNextPage()
        }
    }
    
    // Estado para el Modal
    var showModal by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<DictionaryItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

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

        BasicTextField(
            value = query,
            onValueChange = { viewModel.onSearch(it) },
            singleLine = true,
            cursorBrush = SolidColor(Color.Black),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 80.dp)
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
                    if (query.isEmpty()) {
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

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(words) { item ->
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
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
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
