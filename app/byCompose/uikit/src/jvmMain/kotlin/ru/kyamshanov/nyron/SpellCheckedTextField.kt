package ru.kyamshanov.nyron

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sun.jna.platform.win32.COM.IUnknown
import com.sun.jna.platform.win32.COM.Unknown
import com.sun.jna.platform.win32.Guid
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.Ole32Util
import com.sun.jna.platform.win32.WTypes
import com.sun.jna.ptr.PointerByReference

@Composable
actual fun CustomTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
){
    var hasError by remember { mutableStateOf(false) }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (hasError) Color.Red else MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
            .padding(8.dp),
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
            val errorDetected = checkSpellingWithWindowsAPI(newValue)
            hasError = errorDetected
            if (errorDetected) println("Ошибка")
        }
    )
}

private fun checkSpellingWithWindowsAPI(text: String): Boolean {
    Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED)
    return try {
        val spellCheckerFactoryCLSID = Ole32Util.getGUIDFromString(SpellCheckerConstants.CLSID_SpellCheckerFactory)
        val spellCheckerFactoryPtr = PointerByReference()

        Ole32.INSTANCE.CoCreateInstance(
            spellCheckerFactoryCLSID,
            null,
            WTypes.CLSCTX_INPROC_SERVER,
            Ole32Util.getGUIDFromString(SpellCheckerConstants.IID_ISpellCheckerFactory),
            spellCheckerFactoryPtr
        )

        val spellCheckerFactory = Unknown(spellCheckerFactoryPtr.value)
        val spellCheckerPtr = PointerByReference()
        (spellCheckerFactory.QueryInterface(
            Guid.REFIID(Ole32Util.getGUIDFromString(SpellCheckerConstants.IID_ISpellCheckerFactory).pointer),
            spellCheckerPtr
        ) as ISpellCheckerFactory).GetSpellChecker(null, spellCheckerPtr)

        val spellChecker = Unknown(spellCheckerPtr.value)
        val errorsPtr = PointerByReference()
        (spellChecker.QueryInterface(
            Guid.REFIID(Ole32Util.getGUIDFromString(SpellCheckerConstants.IID_ISpellChecker).pointer),
            errorsPtr
        ) as ISpellChecker).Check(text, errorsPtr)

        val errors = Unknown(errorsPtr.value)
        val errorCount = (errors.QueryInterface(
            Guid.REFIID(Ole32Util.getGUIDFromString(SpellCheckerConstants.IID_ISpellingError).pointer),
            null
        ) as ISpellingError).getCount()

        errors.Release()
        spellChecker.Release()
        spellCheckerFactory.Release()

        errorCount > 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } finally {
        Ole32.INSTANCE.CoUninitialize()
    }
}

// COM интерфейсы
interface ISpellCheckerFactory : IUnknown {
    fun GetSpellChecker(languageTag: String?, spellChecker: PointerByReference): Int
}

interface ISpellChecker : IUnknown {
    fun Check(text: String, errors: PointerByReference): Int
}

interface ISpellingError : IUnknown {
    fun getCount(): Int
}

object SpellCheckerConstants {
    const val CLSID_SpellCheckerFactory = "{7AB36653-1796-484B-BDFA-E74F1DB7C1DC}"
    const val IID_ISpellCheckerFactory = "{8E018A9D-2415-4677-BF08-794EA61F94BB}"
    const val IID_ISpellChecker = "{B6FD0B71-E2BC-4653-8D05-F197E412770B}"
    const val IID_ISpellingError = "{803E3BD4-2828-4410-8290-418D1D73C762}"
}
