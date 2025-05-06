package ru.kyamshanov.nyron

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.kyamshanov.nyron.ListComponent.Model

class DefaultListComponent(
    componentContext: ComponentContext,
    private val onItemClickedListener: (item: String) -> Unit,
) : ListComponent, ComponentContext by componentContext {

    override val model: Value<Model> =
        MutableValue(Model(items = List(100) { "Item $it" }))

    override fun onItemClicked(item: String) {
        onItemClickedListener(item)
    }
}