package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper.assertEquals
import me.senseiwells.arucas.util.TestHelper.throwsCompile
import me.senseiwells.arucas.util.TestHelper.throwsRuntime
import org.junit.jupiter.api.Test

class ArucasExpressionTests {
    @Test
    fun testLiteralExpression() {
        assertEquals(true, "return true;")
        assertEquals(false, "return false;")
        assertEquals("foobar", "return \"foobar\";")
        assertEquals("barfoo", "return 'barfoo';")
        assertEquals(16, "return 0x10;")
        assertEquals(10, "return 10;")
        assertEquals(2.2, "return 2.2;")
        assertEquals(null, "return null;")
    }

    @Test
    fun testBracketExpression() {
        assertEquals(6, "return 1 + 1 * 5;")
        assertEquals(10, "return (1 + 1) * 5;")
        assertEquals(88, "return 93 ~ 10 >> 1;")
        assertEquals(88, "return 93 ~ (10 >> 1);")
        assertEquals(43, "return (93 ~ 10) >> 1;")
        assertEquals(true, "return true && true ~ false || false && true;")
        assertEquals(false, "return true && (true ~ true || false && false);")
        assertEquals(true, "return true & true ~ (false || false) && true;")
    }

    @Test
    fun testListExpression() {
        assertEquals("[0, 1, 2]", "return [0, 1, 2].toString();")
        assertEquals("[[[[]]], [], [[[], []]]]", "return [[[[]]], [], [[[], []]]].toString();")
    }

    @Test
    fun testMapExpression() {
        assertEquals("{1: one, 3: three}", "return {1: 'one', 3: 'three'}.toString();")
        assertEquals("{{{}: {{}: {}}}: [{}, {}, {{}: {}}]}", "return {{{}: {{}: {}}}: [{}, {}, {{}: {}}]}.toString();")
    }

    @Test
    fun testFunctionExpression() {
        assertEquals(10, "x = fun() 10; return x();")
        assertEquals(66,
            """
            x = fun() x = 33;
            return x() + x;
            """
        )
        assertEquals(50,
            """
            return (fun() { return 50; })();    
            """
        )
    }

    @Test
    fun testUnaryExpression() {
        assertEquals(-10, "return -10;")
        assertEquals(-10, "x = 10; return -x;")
        assertEquals(1, "x = 1; return - - - - - - - -x;")
        assertEquals(7, "return +7;")
        assertEquals(false, "return !!!true;")
    }

    @Test
    fun testBinaryExpression() {
        assertEquals(10, "return 5 + 5;")
        assertEquals("foobar", "return 'foo' + 'bar';")
        assertEquals(25, "return 5 ^ 2;")
        assertEquals(false, "return true == false;")
        assertEquals(true, "return 6 > 5;")
        assertEquals(false, "return 6 < 6;")
    }

    @Test
    fun testUnpackAssignExpression() {
        assertEquals(16,
            """
            x, y, z = [4, 4, 4];
            return x + y + z * 2;
            """
        )
        assertEquals("ab",
            """
            fun a() {
                x, y = ["a", "b"];
                return x + y;
            }
            return a();
            """
        )
        assertEquals(168,
            """
            class E { 
                static var e = 10;
            }
            E.e, a, b = [5, 6, 7];
            return E.e * 31 + a + b;    
            """
        )
        assertEquals("310",
            """
            l = [999];
            class E {
                var a = null;
            }
            e = new E();
            l[0], e = [31, 0];
            return l[0].toString() + e;    
            """
        )
    }

    @Test
    fun testAccessExpression() {
        assertEquals(10, "x = 10; return x;")
        throwsRuntime("{ x = 10; } return x;")
    }

    @Test
    fun testFunctionAccessExpression() {
        assertEquals(20,
            """
            fun overload() return 10;
            fun overload(n) return n + 1;
            delegate = overload;
            return delegate() + delegate(9);
            """
        )
        assertEquals(70,
            """
            delegate = range;
            total = 0;
            foreach (i : delegate(5)) {
                total = total + i;
            }
            foreach (i : delegate(10, 15)) {
                total = total + i;
            }
            return total;    
            """
        )
    }

    @Test
    fun testAssignExpression() {
        throwsCompile("fun f(); f = 10;")
        throwsCompile("class f { } f = 10;")
        throwsCompile("{ local f = 10; local f = 11; }")
        assertEquals(true, "return f = true;")
        assertEquals("[true]", "(f = []).append(true); return f.toString();")
        assertEquals(null, "f = null; { local f = 10; { f = 11; } } return f;")
        assertEquals(11, "{ local f = 10; { f = 11; } return f; }")
    }

    @Test
    fun testCallExpression() {
        throwsRuntime("''();")
        throwsRuntime("x = null; x();")
        assertEquals(10,
            """
            x = fun() { return 10; };    
            return x();
            """
        )
        assertEquals(10,
            """
            x = fun() { return 10; };    
            return (x)();
            """
        )
        assertEquals(10, "return fun() { return 10; }();")
    }

    @Test
    fun testThisExpression() {
        throwsCompile("this;")
        throwsCompile("{ this; }")
        assertEquals("e",
            """
            x = null;
            class E {
                E() {
                    x = fun() this;
                }
                
                fun toString() return "e";
            }
            new E();
            return x().toString();
            """
        )
    }

    @Test
    fun testSuperExpression() {
        throwsCompile("super;")
        throwsCompile("{ super; }")
        assertEquals(21,
            """
            class A {
                fun test() return 10;
            }
            class E: A {
                E(): super();
                fun test() return 11;
                fun supertest() return super.test();
            }
            e = new E();
            return e.test() + e.supertest();
            """
        )
        assertEquals("ez",
            """
            class E: List {
                E(): super();
                operator [] (index) {
                    return super[index] + "z";
                }
            }
            e = new E();
            e.append("e");
            return e[0];
            """
        )
    }

    @Test
    fun testMemberAccessExpression() {
        assertEquals(10,
            """
            class E {
                var f = 10;
            }
            return new E().f;
            """
        )
        assertEquals(10,
            """
            class A { var a = 10; }    
            class B { var b = new A(); }
            class C { var c = new B(); }
            class D { var d = new C(); }
            return new D().d.c.b.a;
            """
        )
        throwsRuntime("[].field;")
        throwsRuntime("class A { } new A().field;")
    }

    @Test
    fun testNewCallExpression() {
        throwsRuntime("new String();")
        throwsRuntime("new NonExistant();")
        throwsRuntime("class C { C(arg); } new C();")
        assertEquals("[10, 9, 8]", "class C { var field; C(args...) this.field = args; } return new C(10, 9, 8).field.toString();")
        assertEquals(10, "class C { var field; C(a, b, c) this.field = a; } return new C(10, 9, 8).field;")
        assertEquals(9, "class C { var field; C(a, b, c) this.field = b; } return new C(10, 9, 8).field;")
    }

    @Test
    fun testBracketAccessExpression() {
        throwsRuntime("l = []; l[0];")
        assertEquals(0, "l = [0]; return l[0];")
    }

    @Test
    fun testBracketAssignExpression() {
        throwsRuntime("l = []; l[0] = 10;")
        assertEquals(10, "l = [0]; return l[0] = 10;")
    }
}