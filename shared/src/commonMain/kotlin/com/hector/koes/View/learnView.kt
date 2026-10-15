package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.components.card.ModalOracion
import com.hector.koes.data.OracionData
import com.hector.koes.model.Oracion
import com.hector.koes.ui.theme.Background

@Composable
fun LearnView(
    onNavigate: (String) -> Unit = {},
    showNavbar: Boolean = true
) {
    var selectedOracion by remember {
        mutableStateOf<Oracion?>(null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (showNavbar) {
            Navbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 10.dp),
                onNavigate = onNavigate
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()

        ) {
            Text(
                text = "Formación de palabras",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 80.dp, start = 21.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = OracionData.oraciones,
                    key = { it.id }
                ) { oracion ->
                    OracionItem(
                        titulo = oracion.titulo,
                        onClick = {
                            selectedOracion = oracion
                        }
                    )
                }
            }
        }
    }

    selectedOracion?.let { oracion ->
        ModalOracion(
            oracion = oracion,
            onDismiss = {
                selectedOracion = null
            }
        )
    }
}

@Composable
fun OracionItem(
    titulo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .widthIn(max = 362.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = Color(0xFFDAE4EA),
                shape = RoundedCornerShape(5.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 800
)
@Composable
fun LearnViewPreview() {
    LearnView(showNavbar = false)
}
