package dev.implario.bukkit

import org.bukkit.Location
import org.bukkit.entity.Entity

inline fun <reified T: Entity> Location.spawn(action : T.() -> Unit = {}): T =
    world.spawn(this, T::class.java).also(action)