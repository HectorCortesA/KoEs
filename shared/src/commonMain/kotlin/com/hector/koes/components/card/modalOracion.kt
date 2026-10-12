
package com.hector.koes.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hector.koes.model.*
import kotlinx.coroutines.launch

private val SecondaryText = Color(0xFF6C6C6C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalOracion(
    oracion: Oracion,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(
            topStart = 40.dp,
            topEnd = 40.dp
        ),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 95.dp,
                height = 5.dp,
                color = Color(0xFF555555)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 28.dp)
                .padding(top = 32.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = oracion.titulo,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Text(
                text = oracion.descripcion,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color.Black
            )

            oracion.estructuras.forEach { estructura ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionTitle(estructura.titulo)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        estructura.palabras.forEach { palabra ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = palabra.palabra,
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = palabra.funcion,
                                    fontSize = 15.sp,
                                    color = SecondaryText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            if (oracion.significados.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionTitle("¿Qué significa cada palabra?")

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TableText("Coreano", Modifier.weight(1f), true)
                        TableText(
                            "Pronunciación aproximada",
                            Modifier.weight(2.3f),
                            true
                        )
                        TableText(
                            "Significado",
                            Modifier.weight(1.8f),
                            true
                        )
                    }

                    oracion.significados.forEach { palabra ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            TableText(
                                palabra.coreano,
                                Modifier.weight(1f)
                            )
                            TableText(
                                palabra.pronunciacion,
                                Modifier.weight(2.3f)
                            )
                            TableText(
                                palabra.significado,
                                Modifier.weight(1.8f)
                            )
                        }
                    }
                }
            }

            if (oracion.comparaciones.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionTitle("Comparación con otros verbos")

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.width(620.dp)
                        ) {
                            TableText("Verbo", Modifier.weight(1.3f), true)
                            TableText("Presente", Modifier.weight(1f), true)
                            TableText("Pasado", Modifier.weight(1f), true)
                            TableText("Futuro", Modifier.weight(1.4f), true)
                        }

                        oracion.comparaciones.forEach { verbo ->
                            Row(
                                modifier = Modifier.width(620.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                TableText(verbo.verbo, Modifier.weight(1.3f))
                                TableText(verbo.presente, Modifier.weight(1f))
                                TableText(verbo.pasado, Modifier.weight(1f))
                                TableText(verbo.futuro, Modifier.weight(1.4f))
                            }
                        }
                    }
                }
            }

            if (oracion.ejemplos.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionTitle("Ejemplos")

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TableText("Coreano", Modifier.weight(1f), true)
                        TableText("Español", Modifier.weight(1f), true)
                    }

                    oracion.ejemplos.forEach { ejemplo ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            TableText(
                                ejemplo.coreano,
                                Modifier.weight(1f)
                            )
                            TableText(
                                ejemplo.espanol,
                                Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )
}

@Composable
private fun TableText(
    value: String,
    modifier: Modifier = Modifier,
    header: Boolean = false
) {
    Text(
        text = value,
        modifier = modifier.padding(end = 8.dp),
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = if (header) FontWeight.Medium else FontWeight.Normal,
        color = Color.Black
    )
}
