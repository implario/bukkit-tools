package dev.implario.bukkit.item

import org.bukkit.inventory.ItemStack

fun itemBuilder(builder: ItemBuilder.() -> Unit) = ItemBuilder()
    .also(builder)

fun item(builder: ItemBuilder.() -> Unit): ItemStack = ItemBuilder()
    .also(builder).build()
