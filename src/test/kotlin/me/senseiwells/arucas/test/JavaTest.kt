package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper
import org.junit.jupiter.api.Test

class JavaTest {
    @Test
    fun testTypeConversions() {

    }

    @Test
    fun testReflection() {
        TestHelper.assertEquals("DEFAULT",
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            return ReflectionTestHelper.STRING_CONSTANT;
            """, TestHelper.API_JAVA
        )
        TestHelper.assertEquals(11,
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            ReflectionTestHelper.INT_CONSTANT = (ReflectionTestHelper.INT_CONSTANT + 1).intValue();
            return ReflectionTestHelper.INT_CONSTANT.toArucas();
            """, TestHelper.API_JAVA
        )
        TestHelper.throwsRuntime(
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            ReflectionTestHelper.IMMUTABLE_CONSTANT = 10;
            """, TestHelper.API_JAVA
        )
        TestHelper.assertEquals(true,
            """
            ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
            return ReflectionTestHelper().returnObjectMethod() == ReflectionTestHelper.IMMUTABLE_CONSTANT;
            """, TestHelper.API_JAVA
        )
        // FixMe:
        // TestHelper.assertEquals("[1, 2, 3]",
        //    """
        //    ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
        //    return ReflectionTestHelper.returnVarargsAsList(1, 2, 3).toString();
        //    """, TestHelper.API_JAVA
        //)
        // TestHelper.assertEquals(4,
        //    """
        //    ReflectionTestHelper = Java.classOf("me.senseiwells.arucas.test.java.ReflectionTestHelper");
        //    return ReflectionTestHelper.returnIntPlusOneParameterMethod(3).toArucas();
        //    """, TestHelper.API_JAVA
        //)
    }
}