package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hector.koes.components.Profile.ModaProfile
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.ui.theme.Background
import com.hector.koes.viewModel.SettingsManager
import com.hector.koes.viewModel.WritingMode

@Composable
fun SettingsView(
    onNavigate: (String) -> Unit = {},
    name: String = "Hector Uriel A"
) {
    val viewModel = SettingsManager.instance
    val scrollState = rememberScrollState()
    
    var profileName by remember(name) { mutableStateOf(name) }
    var showProfileModal by remember { mutableStateOf(false) }

    val writingMode by viewModel.writingMode.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (showProfileModal) 16.dp else 0.dp)
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
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(
                        top = 80.dp,
                        start = 21.dp,
                        end = 21.dp
                    )
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = profileName,
                        modifier = Modifier.width(220.dp),
                        fontSize = 36.sp,
                        fontWeight = FontWeight(600),
                        lineHeight = 38.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .width(104.dp)
                            .height(106.dp)
                            .background(
                                color = Color(0x33D9D9D9),
                                shape = RoundedCornerShape(50.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "IMG",
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                SettingsOption(
                    text = "Perfil",
                    onClick = { showProfileModal = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsOption(
                    text = "Tema"
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsWritingOption(
                    palabrasActivas = writingMode == WritingMode.PALABRAS,
                    onPalabrasChange = { if (it) viewModel.setWritingMode(WritingMode.PALABRAS) },
                    oracionesActivas = writingMode == WritingMode.ORACIONES,
                    onOracionesChange = { if (it) viewModel.setWritingMode(WritingMode.ORACIONES) },
                    selectedCategory = selectedCategory,
                    categories = categories,
                    onCategorySelected = { viewModel.setCategory(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsOption(
                    text = "Guardado of datos"
                )

                Spacer(modifier = Modifier.height(16.dp))
                SettingsClosetOption(
                    text = "Cerrar sesión"
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showProfileModal) {
            ModaProfile(
                nameProfile = profileName,
                photoUrl = "",
                onBack = { showProfileModal = false },
                onUpdateProfile = { newName ->
                    profileName = newName
                    showProfileModal = false
                }
            )
        }
    }
}

@Composable
fun SettingsOption(
    text: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(49.dp)
            .background(
                color = Color(0xFFF8F8F8),
                shape = RoundedCornerShape(30.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}
@Composable
fun SettingsClosetOption(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(49.dp)
            .background(color = Color(0xFF595959), shape = RoundedCornerShape(size = 30.dp))

            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun SettingsWritingOption(
    palabrasActivas: Boolean,
    onPalabrasChange: (Boolean) -> Unit,
    oracionesActivas: Boolean,
    onOracionesChange: (Boolean) -> Unit,
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8F8F8),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(
                horizontal = 24.dp,
                vertical = 18.dp
            )
    ) {

        Text(
            text = "Escritura",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Palabras",
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            CustomSmallCheckbox(
                checked = palabrasActivas,
                onCheckedChange = onPalabrasChange
            )

            Spacer(modifier = Modifier.width(35.dp))

            Text(
                text = "Oraciones",
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            CustomSmallCheckbox(
                checked = oracionesActivas,
                onCheckedChange = onOracionesChange
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Categoría",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(43.dp)
                .background(
                    color = Color(0x33D9D9D9),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selectedCategory,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomSmallCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(15.dp)
            .height(15.dp)
            .background(
                color = if (checked) {
                    Color(0xFF5B5B5B)
                } else {
                    Color(0x335B5B5B)
                },
                shape = RoundedCornerShape(size = 100.dp)
            )
            .clickable {
                onCheckedChange(!checked)
            }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsViewPreview() {
    SettingsView()
}
