package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper
import org.junit.jupiter.api.Test

class StringTests {
    @Test
    fun stringMethodTests() {
        TestHelper.assertEquals("Hello World",
            """
            return "%s %s".format("Hello", "World");    
            """
        )
    }
}