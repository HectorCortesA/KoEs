package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.ui.theme.Background

@Composable
fun settingsView(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Navbar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )
    }

}

@Preview(
    showBackground = true
)
@Composable
fun settingsViewPreview(){
    settingsView()
}