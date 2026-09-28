package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hector.koes.components.card.CardDictionary
import com.hector.koes.components.card.ModalDictionary
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.model.DictionaryItem
import com.hector.koes.ui.theme.Background
import com.hector.koes.viewModel.FavoritesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteView(
    onNavigate: (String) -> Unit = {}
) {
    val favoritesList by FavoritesManager.favorites.collectAsState()
    var visibleCount by remember { mutableStateOf(20) }

    val listState = rememberLazyListState()

    // Detectar scroll para cargar 20 más progresivamente
    val shouldLoadMore = remember {
        derivedStateOf {
            val total = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && visibleCount < favoritesList.size) {
            visibleCount = (visibleCount + 20).coerceAtMost(favoritesList.size)
        }
    }

    // Estado para el Modal
    var showModal by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<DictionaryItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val currentVisibleFavorites = favoritesList.take(visibleCount)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // Navbar superior
        Navbar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 10.dp),
            onNavigate = onNavigate
        )

        // Título "Favoritos" alineado a la izquierda debajo de Navbar
        Text(
            text = "Favoritos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 80.dp, start = 21.dp)
        )

        if (favoritesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 130.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes favoritos guardados aún.",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 130.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(currentVisibleFavorites) { item ->
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

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // ModalBottomSheet para detalle de favorito
        if (showModal && selectedItem != null) {
            val item = selectedItem!!
            val isFav = FavoritesManager.isFavorite(item)

            ModalBottomSheet(
                onDismissRequest = { showModal = false },
                sheetState = sheetState,
                containerColor = Color(0x80FFFFFF),
                dragHandle = null,
                tonalElevation = 0.dp,
                scrimColor = Color.Black.copy(alpha = 0.32f),
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth().background(Color(0x80FFFFFF))) {
                    ModalDictionary(
                        wordSpanish = item.spanish,
                        wordCorea = item.korean,
                        romanization = item.romanization,
                        pronunciation = item.pronunciation,
                        ejemploSpanish = item.ejemploSpanish,
                        ejemploKoreano = item.ejemploKoreano,
                        isFavorite = isFav,
                        onToggleFavorite = {
                            FavoritesManager.toggleFavorite(item)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritePreview() {
    FavoriteView()
}
