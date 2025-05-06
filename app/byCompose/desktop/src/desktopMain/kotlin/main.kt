import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.jetbrains.compose.resources.painterResource
import nyron.app.bycompose.desktop.generated.resources.Res
import nyron.app.bycompose.desktop.generated.resources.app_icon
import ru.kyamshanov.nyron.App
import ru.kyamshanov.nyron.DefaultRootComponent
import ru.kyamshanov.nyron.RootComponent


fun main() {
    //init DI

    val lifecycle = LifecycleRegistry()

    // Always create the root component outside Compose on the UI thread
    val root: RootComponent =
        runOnUiThread {
            DefaultRootComponent(
                componentContext = DefaultComponentContext(lifecycle = lifecycle),
            )
        }

    application {
        val windowState = rememberWindowState()

        LifecycleController(lifecycle, windowState)

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "nyron",
            icon = painterResource(Res.drawable.app_icon) //for generate Res class use `gradle :app:byCompose:desktop:generateComposeResClass`
        ) {
            App(root)
        }
    }
}