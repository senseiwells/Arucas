package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper.compile
import me.senseiwells.arucas.util.TestHelper.throwsCompile
import org.junit.jupiter.api.Test

class SyntaxTests {
    @Test
    fun testPrivateSyntax() {
        compile(
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
        compile(
            """
            class X {
                readonly var a;
                readonly var b: Number;
                readonly var c: String = "";
                private readonly var d;
                private static readonly var e = 9;
            }
            """
        )
        throwsCompile("readonly;")
        throwsCompile("class X { readonly; }")
        throwsCompile("class X { readonly fun x(); }")
        throwsCompile("class X { readonly {} }")
        throwsCompile("class X { readonly X(){} }")
        throwsCompile("class X { readonly static; }")
    }
}