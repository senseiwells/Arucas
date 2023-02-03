package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper
import org.junit.jupiter.api.Test

class JavaTest {
    @Test
    fun testTypeConversions() {
        TestHelper.assertEquals("[0]", "return Java.intArray(1).toArucas().toString();", TestHelper.API_JAVA)
        TestHelper.assertEquals(":)", "return Java.valueOf('%s').formatted(':)').toArucas();", TestHelper.API_JAVA)
    }

    @Test
    fun testFields() {
        TestHelper.assertEquals("DEFAULT",
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            return ReflectionTestHelper.STRING_CONSTANT;
            """, TestHelper.API_JAVA
        )
        TestHelper.assertEquals(11,
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            ReflectionTestHelper.INT_CONSTANT = ReflectionTestHelper.INT_CONSTANT + 1;
            return ReflectionTestHelper.INT_CONSTANT.toArucas();
            """, TestHelper.API_JAVA
        )
        TestHelper.throwsRuntime(
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            ReflectionTestHelper.IMMUTABLE_CONSTANT = 10;
            """, TestHelper.API_JAVA
        )
    }

    @Test
    fun testMethods() {
        TestHelper.assertEquals("[1, 2, 3]",
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            return ReflectionTestHelper.returnVarargsAsList(1, 2, 3).toString();
            """, TestHelper.API_JAVA
        )
        TestHelper.assertEquals(true,
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            return ReflectionTestHelper().returnObjectMethod() == ReflectionTestHelper.IMMUTABLE_CONSTANT;
            """, TestHelper.API_JAVA
        )

        TestHelper.assertEquals(4,
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            return ReflectionTestHelper.returnIntPlusOneParameterMethod(3).toArucas();
            """, TestHelper.API_JAVA
        )
    }

    @Test
    fun testOperators() {
        TestHelper.assertEquals(28, "return Java.intOf(15) + Java.doubleOf(13);", TestHelper.API_JAVA)
        TestHelper.assertEquals(30, "return Java.doubleOf(6) * 5;", TestHelper.API_JAVA)
        TestHelper.assertEquals(HashMap<Any, Any>().apply { put("Hello!", 90.0) },
            """
            HashMap = Java.classOf("java.util.HashMap");
            map = HashMap();
            map["Hello!"] = 90;
            return map;
            """, TestHelper.API_JAVA
        )
        TestHelper.assertEquals(100,
            """
            array = Java.byteArray(10);
            array[0] = 0x10;
            array[9] = 84;
            return array[0] + array[9] + 0.0; // Convert to double
            """, TestHelper.API_JAVA
        )
    }
}