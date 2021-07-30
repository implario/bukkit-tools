package dev.implario.bukkit.test

import dev.implario.bukkit.item.item
import org.bukkit.Material
import org.junit.jupiter.api.Test

class ItemBuilderTest {

    @Test
    fun testItemBuilder() {

         val stack = item {
             type = Material.STONE
             amount = 64
             text("hello\nworld")
         }

    }

}
