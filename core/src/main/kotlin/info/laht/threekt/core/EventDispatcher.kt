package info.laht.threekt.core

class Event(
    val type: String,
    val target: Any?
)

fun interface EventLister {

    fun onEvent(event: Event)

}

interface EventDispatcher {

    /**
     * Adds a listener to an event type.
     * @param type The type of event to listen to.
     * @param listener The function that gets called when the event is fired.
     */
    fun addEventListener(type: String, listener: EventLister)

    /**
     * Checks if listener is added to an event type.
     * @param type The type of event to listen to.
     * @param listener The function that gets called when the event is fired.
     */
    fun hasEventListener(type: String, listener: EventLister): Boolean

    /**
     * Removes a listener from an event type.
     * @param type The type of the listener that gets removed.
     * @param listener The listener function that gets removed.
     */
    fun removeEventListener(type: String, listener: EventLister)

    /**
     * Fire an event type.
     */
    fun dispatchEvent(type: String, target: Any)

}

class EventDispatcherImpl : EventDispatcher {

    private val listeners by lazy { mutableMapOf<String, MutableList<EventLister>>() }

    override fun addEventListener(type: String, listener: EventLister) {

        if (type !in listeners) {
            listeners[type] = mutableListOf()
        }

        if (listener !in listeners.getValue(type)) {
            listeners.getValue(type).add(listener)
        }

    }

    override fun hasEventListener(type: String, listener: EventLister): Boolean {
        return listeners[type]?.contains(listener) == true
    }

    override fun removeEventListener(type: String, listener: EventLister) {
        listeners[type]?.remove(listener)
    }

    override fun dispatchEvent(type: String, target: Any) {
        //toMutableList avoids ConcurrentModificationException
        listeners[type]?.toMutableList()?.forEach {
            it.onEvent(Event(type, target))
        }
    }

}
