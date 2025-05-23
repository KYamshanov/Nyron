package ru.kyamshanov.nyron

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(rootComponent: RootComponent, modifier: Modifier = Modifier.fillMaxSize()) {
    ComposableAppTheme {
        Surface {
            RootContent(component = rootComponent, modifier = Modifier.fillMaxSize())
        }
    }
}