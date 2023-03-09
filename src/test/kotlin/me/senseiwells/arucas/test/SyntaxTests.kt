package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper
import me.senseiwells.arucas.util.TestHelper.throwsCompile
import org.junit.jupiter.api.Test

class SyntaxTests {
    @Test
    fun testPrivateSyntax() {
        TestHelper.compile(
            """
            class X {
                private var a;
                private var b: Number;
                private X();
                private X(a, b, c);
                private X(args...);
                private fun foo();
                private fun bar(a, b, c);
                private fun baz(args...) { }
                private static fun foobar();
            }
            """
        )
        throwsCompile("class X { private readonly X(); }")
        throwsCompile("class X { private(); }")
        throwsCompile("class X { private A(); }")
        throwsCompile("class X { private; }")
        throwsCompile("class X { private(); }")
        throwsCompile("class X { private { } }")
    }

    @Test
    fun testReadonlySyntax() {
        throwsCompile("readonly;")
        throwsCompile("class X { readonly; }")
    }
}