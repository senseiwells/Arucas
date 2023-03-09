package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper.assertEquals
import me.senseiwells.arucas.util.TestHelper.throwsRuntime
import org.junit.jupiter.api.Test

class VisibilityTests {
    @Test
    fun testPrivateConstructors() {
        throwsRuntime(
            """
            class X { private X(); }    
            new X();
            """
        )
        throwsRuntime(
            """
            class A { private A(); }
            class B: A { B(): super(); }
            new B();
            """
        )
        throwsRuntime(
            """
            class A { private A(); }
            class B { 
                static fun of() {
                    return new A();
                }
            }
            B.of();
            """
        )
        assertEquals(10,
            """
            class X {
                var f = 10;
                private X();
                static fun of() {
                    return new X();
                }
            }
            return X.of().f;
            """
        )
        assertEquals(9,
            """
            class Factory {
                var f = 9;
                private Factory();
                static fun g() {
                    return fun() new Factory();
                }
            }
            return Factory.g()().f;
            """
        )
    }

    @Test
    fun testPrivateFields() {
        throwsRuntime(
            """
            class X { private var foo; }
            new X().foo;
            """
        )
        throwsRuntime(
            """
            class A { private var x; }
            class B: A { B(): super() { this.x; } }
            new B();
            """
        )
        assertEquals("bar",
            """
            class X {
                private var foo;
                
                fun setFoo(v) {
                    this.foo = v;
                }
                
                fun getFoo() {
                    return this.foo;
                }
            }
            x = new X();
            x.setFoo("bar");
            return x.getFoo();
            """
        )
    }

    @Test
    fun testPrivateMethods() {
        throwsRuntime("class X { private fun toString(); }")
        throwsRuntime(
            """
            class A { fun foo(); }
            class B: A { B(): super(); private fun foo(); }
            """
        )
        throwsRuntime(
            """
            interface A { fun foo(); }
            class B: A { B(): super(); private fun foo(); }
            """
        )
        throwsRuntime(
            """
            class X { private fun foo(); }
            new X().foo();
            """
        )
        throwsRuntime(
            """
            class X { private static fun foo(); }
            X.foo();
            """
        )
        throwsRuntime(
            """
            class A { private fun x(); }
            class B: A { B(): super() { this.x(); } }
            new B();
            """
        )
        assertEquals(5,
            """
            class X { private fun foo() return 5; fun access() return this.foo(); }
            return new X().access();
            """
        )
        assertEquals(58,
            """
            class X { private static fun foo() return 58; static fun access() return X.foo(); }
            return X.access();
            """
        )
        assertEquals(0,
            """
            class X {
                private fun foo() {
                    return 0;
                }
                
                fun del() {
                    return this.foo;
                }
            }
            return new X().del()();
            """
        )
        assertEquals(0,
            """
            class X {
                private static fun foo() {
                    return 0;
                }
                
                static fun del() {
                    return X.foo;
                }
            }
            return X.del()();
            """
        )
    }

    @Test
    fun testReadonlyFields() {
        throwsRuntime(
            """
            class X {
                readonly var a;
                X(a) { this.a = a; }
            }
            new X(0).a = 10;
            """
        )
        assertEquals(10,
            """
            class X {
                readonly var a;
                X(a) { this.a = a; }
            }
            return new X(10).a;
            """
        )
        assertEquals("foo",
            """
            class X {
                readonly var a;
                X() { this.defaults(); }
                fun defaults() { this.a = "foo"; }
            }
            return new X().a;
            """
        )
        assertEquals("bar",
            """
            class A { readonly var a; }
            class B: A {
                B(): super() {
                    this.a = "bar";
                }
            }
            new B();
            return new B().a;
            """
        )
    }

    @Test
    fun testPrivateReadonlyFields() {

    }
}