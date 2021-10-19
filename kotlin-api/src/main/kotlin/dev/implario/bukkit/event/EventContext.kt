package dev.implario.bukkit.event

import org.bukkit.event.Event
import org.bukkit.event.EventPriority

inline fun <reified T : Event> EventContext.on(
    priority: EventPriority = EventPriority.NORMAL,
    noinline handler: T.() -> Unit
) = on(T::class.java, priority, handler)

inline fun <reified T : Event> EventContext.after(noinline handler: T.() -> Unit) =
    on(priority = EventPriority.HIGH, handler)

inline fun <reified T : Event> EventContext.before(noinline handler: T.() -> Unit) =
    on(priority = EventPriority.LOW, handler)
