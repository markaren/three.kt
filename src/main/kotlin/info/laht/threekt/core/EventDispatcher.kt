package info.laht.threekt.core

typealias EventLister = () -> Unit

open class EventDispatcher {

    private val listeners = mutableMapOf<String, MutableList<EventLister>>()

    /**
     * Adds a listener to an event type.
     * @param type The type of event to listen to.
     * @param listener The function that gets called when the event is fired.
     */
    fun addEventListener( type: String, listener: EventLister ) {
        listeners.computeIfAbsent(type) { mutableListOf() }.also {
            it.add(listener)
        }
    }

    /**
     * Checks if listener is added to an event type.
     * @param type The type of event to listen to.
     * @param listener The function that gets called when the event is fired.
     */
    fun hasEventListener( type: String, listener: EventLister ): Boolean {
        return listeners[type]?.contains { listener } == true
    }

    /**
     * Removes a listener from an event type.
     * @param type The type of the listener that gets removed.
     * @param listener The listener function that gets removed.
     */
    fun removeEventListener( type: String, listener: EventLister ) {
        listeners[type]?.remove { listener }
    }

    /**
     * Fire an event type.
     */
    fun dispatchEvent( type: String ) {
        listeners[type]?.forEach {
            it.invoke()
        }
    }

}
