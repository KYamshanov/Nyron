package ru.kyamshanov.nyron

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.languagetool.JLanguageTool
import org.languagetool.Languages

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun CustomTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    language: String
) {
    var showSuggestions by remember { mutableStateOf(false) }
    var clickedError by remember { mutableStateOf<LanguageToolError?>(null) }

    val languageTool = remember {
        try {
            JLanguageTool(Languages.getLanguageForShortCode(language))
        } catch (e: Exception) {
            null
        }
    }
    val visualTransformation = remember(value, languageTool) {
        VisualTransformation { text ->
            val annotatedString = buildAnnotatedString {
                append(text.text)

                if (languageTool != null) {
                    try {
                        val matches = languageTool.check(text.text)
                        for (match in matches) {
                            addStyle(
                                style = SpanStyle(
                                    color = Color.Red,
                                    textDecoration = TextDecoration.Underline
                                ),
                                start = match.fromPos,
                                end = match.toPos
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            TransformedText(annotatedString, OffsetMapping.Identity)
        }
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current,
            modifier = Modifier.onPointerEvent(PointerEventType.Press) { event ->
                if (event.buttons.isSecondaryPressed) {
                    val offset = event.changes.first().position.x.toInt()
                    languageTool?.check(value)?.firstOrNull { error ->
                        offset in error.fromPos..error.toPos
                    }?.let { error ->
                        clickedError = LanguageToolError(
                            fromPos = error.fromPos,
                            toPos = error.toPos,
                            message = error.message,
                            suggestions = error.suggestedReplacements
                        )
                        showSuggestions = true
                    }
                }
            },
            visualTransformation = visualTransformation
        )

        if (showSuggestions && clickedError != null) {
            AlertDialog(
                onDismissRequest = { showSuggestions = false },
                title = { Text("Ошибка: ${clickedError!!}") },
                text = {
                    Column {
                        clickedError!!.suggestions.take(5).forEach { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .clickable {
                                        val newText = value.substring(0, clickedError!!.fromPos) +
                                                suggestion +
                                                value.substring(clickedError!!.toPos)
                                        onValueChange(newText)
                                        showSuggestions = false
                                    }
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSuggestions = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

private data class LanguageToolError(
    val fromPos: Int,
    val toPos: Int,
    val message: String,
    val suggestions: List<String>
)
