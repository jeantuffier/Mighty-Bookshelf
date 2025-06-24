package no.northernfield.mightybookshelf

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


interface EventBus<T> {
    val events: Flow<T>
    fun produceEvent(event: T)
}

fun <T> EventBus() = object : EventBus<T> {
    private val _events = Channel<T>(

    )
    override val events = _events.receiveAsFlow()

    override fun produceEvent(event: T) {
        _events.trySend(event)
            .onFailure { println("Failed to emit $event") }
    }
}
