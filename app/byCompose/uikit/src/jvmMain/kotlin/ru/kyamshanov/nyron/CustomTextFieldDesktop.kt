package ru.kyamshanov.nyron

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuData
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.LocalContentColor
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun CustomTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val spellCheckInteractor = remember { LanguageToolInteractor("ru") }
    var errors by remember { mutableStateOf<List<SpellingError>>(emptyList()) }

    LaunchedEffect(value) {
        errors = spellCheckInteractor.checkSpelling(value)
    }
    Box(modifier = modifier) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            onTextLayout = { textLayoutResult = it },
            textStyle = LocalTextStyle.current,
            modifier = Modifier.fillMaxWidth()
        )

        Canvas(modifier = Modifier.matchParentSize()) {

            /**
             * Рисует волнистое подчеркивание для ошибок орфографии.
             * @param layout Результат измерения текста
             * @param errors Список ошибок с позициями
             */
            fun DrawScope.drawWavyUnderline(
                layout: TextLayoutResult,
                errors: List<SpellingError>
            ) {
                errors.forEach { error ->
                    val start = error.startIndex.coerceAtMost(value.length)
                    val end = error.endIndex.coerceAtMost(value.length)
                    val startX = layout.getHorizontalPosition(start, true)
                    val endX = layout.getHorizontalPosition(end, true)
                    val baseline = layout.getLineBottom(layout.getLineForOffset(start)) - 2.dp.toPx()

                    val path = Path().apply {
                        moveTo(startX, baseline)
                        var x = startX
                        while (x < endX) {
                            quadraticTo(
                                x + 4.dp.toPx(), baseline + 2.dp.toPx(),
                                x + 8.dp.toPx(), baseline
                            )
                            x += 8.dp.toPx()
                        }
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFFFF5252),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }



            textLayoutResult?.let { layout ->
                drawWavyUnderline(layout, errors)
            }
        }
    }
}

