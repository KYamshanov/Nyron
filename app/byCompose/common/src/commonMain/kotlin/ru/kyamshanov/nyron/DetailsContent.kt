package ru.kyamshanov.nyron

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun DetailsContent(component: DetailsComponent, modifier: Modifier = Modifier) {
    val model by component.model.subscribeAsState()
    Column(modifier = modifier.safeDrawingPadding()) {
        CustomButton(onClick = { component.onBack() }) {
            Text(text = model.title)
        }
        var text by rememberSaveable { mutableStateOf("Text") }
        CustomTextField(value = text, onValueChange = { text = it })
    }
}