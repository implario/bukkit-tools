package dev.implario.bukkit.test

import dev.implario.bukkit.item.item
import dev.implario.bukkit.item.itemBuilder
import org.bukkit.Material
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ItemBuilderTest {

    @Test
    fun testItemBuilder() {

         val stack = itemBuilder {
             type = Material.STONE
             amount = 64
             text("hello\nworld")
         }

    }

}
