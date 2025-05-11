package ru.kyamshanov.nyron

/**
 * Определяет базовый интерфейс для взаимодействия с системой проверки орфографии.
 *
 * — Возвращает список ошибок с позициями в тексте
 * — Подсказки запрашиваются отдельно для оптимизации производительности
 * — Предполагает асинхронную работу для избежания блокировки UI
 */
interface SpellCheckInteractor {
    suspend fun checkSpelling(text: String): List<SpellingError>
}

data class SpellingError(
    val startIndex: Int,
    val endIndex: Int,
    val word: String,
    val suggestions: List<String>  // Перенесено из RuleMatch
)


