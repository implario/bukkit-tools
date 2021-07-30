package dev.implario.bukkit.item

fun item(builder: ItemBuilder.() -> Unit) = ItemBuilder().also(builder)