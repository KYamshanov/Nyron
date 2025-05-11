package ru.kyamshanov.nyron

import org.languagetool.JLanguageTool
import org.languagetool.Languages

/**
 * Реализует обновленный SpellCheckInteractor с прямой передачей подсказок из LanguageTool.
 *
 * — Использует suggestedReplacements из RuleMatch без дополнительных запросов
 * — Сохраняет все исходные данные проверки в одном объекте
 * — Упрощает логику работы с меню замены (доступ к suggestions без повторных вызовов)
 * — Требует однократной инициализации JLanguageTool
 */
class LanguageToolInteractor(
    language: String
) : SpellCheckInteractor {
    private val languageTool = JLanguageTool(Languages.getLanguageForShortCode(language))

    override suspend fun checkSpelling(text: String): List<SpellingError> {
        return languageTool.check(text).map { match ->
            SpellingError(
                startIndex = match.fromPos,
                endIndex = match.toPos,
                word = text.substring(match.fromPos, match.toPos),
                suggestions = match.suggestedReplacements
            )
        }
    }
}

