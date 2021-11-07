package dev.implario.bukkit.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

inline fun <reified T : Event> EventContext.on(
    priority: EventPriority = EventPriority.NORMAL,
    noinline handler: T.() -> Unit
) = on(T::class.java, priority, handler)

inline fun <reified T : Event> EventContext.after(
    noinline handler: T.() -> Unit
) = on(priority = EventPriority.HIGH, handler)

inline fun <reified T : Event> EventContext.before(
    noinline handler: T.() -> Unit
) = on(priority = EventPriority.LOW, handler)

inline fun <reified T> EventContext.cancel(
    priority: EventPriority = EventPriority.NORMAL
) where T : Event, T : Cancellable =
    cancel(T::class.java, priority)

inline fun <reified T> EventContext.filterOn(
    priority: EventPriority = EventPriority.NORMAL,
    noinline handler: T.() -> Boolean
) where T : Event, T : Cancellable =
    filterOn(T::class.java, priority) { handler(it) }

inline fun <reified T> EventContext.filterAfter(
    noinline handler: T.() -> Boolean
) where T : Event, T : Cancellable =
    filterOn(priority = EventPriority.HIGH, handler)

inline fun <reified T> EventContext.filterBefore(
    noinline handler: T.() -> Boolean
) where T : Event, T : Cancellable =
    filterOn(priority = EventPriority.LOW, handler)
