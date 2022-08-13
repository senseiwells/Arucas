package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper.assertEquals
import me.senseiwells.arucas.util.TestHelper.throwsRuntime
import org.junit.jupiter.api.Test

class ArucasStatementTests {
    @Test
    fun testScopeStatement() {
        assertEquals(1, "x = 0; { x = 1; } return x;")
        assertEquals(10, "x = 0; { (fun() x = 10)(); } return x;")
        assertEquals(11, "{ x = 10; { x = 11; } return x; }")
        assertEquals(12, "x = 10; class X { static { x = 12; } } return x;")

        assertEquals(100,
            """
            lazy = [];
            for (i = 0; i < 10; i++) {
                lazy.append(fun() i);
            }
            total = 0;
            foreach (l : lazy) {
                total = total + l();
            }
            return total;
            """
        )

        throwsRuntime("{ x = 10; } return x;")
        throwsRuntime("{ { x = 10; } return x; }")
        throwsRuntime("class X { static { x = 10; } } return x;")
        throwsRuntime("if (true) x = 10; return x;")
    }

    @Test
    fun testLocalVarStatement() {
        assertEquals(10, "{ local x = 10; { local x = 11; } return x; }")
        assertEquals(11, "{ local x = 10; { local x = 11; return x; } }")

        throwsRuntime("{ local x = 10; } return x;")
    }

    @Test
    fun testIfStatement() {
        assertEquals(10, "if (true) return 10; else return 0;")
        assertEquals(10, "if (false) return 0; else return 10;")
        assertEquals(10, "if (false) { } else if (true) { return 10; }")

        throwsRuntime("if (null);")
        throwsRuntime("if (10);")
    }

    @Test
    fun testSwitchStatement() {
        assertEquals(1, "switch (null) { default -> return 1; }")
        assertEquals(2,
            """
            switch ("string") {
                case 10 -> return 0;
                case "string" -> return 2;
                default -> return 3;
            }
            """
        )
        assertEquals(0,
            """
            switch ("string") {
                case "string" -> return 0;
                case "string" -> return 2;
                default -> return 3;
            }
            """
        )
        assertEquals(10,
            """
            switch (10) {
                case 5, 6, 7 -> return 9;
                case 8, 9, 10 -> return 10;
            }
            """
        )
        assertEquals(9,
            """
            switch (10) { }
            return 9;
            """
        )
    }

    @Test
    fun testFunctionStatement() {
        assertEquals(1, "fun f() return 1; return f();")
        assertEquals(10,
            """
            x = 0;
            fun test(arg) { 
                x = arg;
            }
            test(10);
            return x;
            """
        )
        assertEquals(1,
            """
            fun f() {
			    return 1;
			}
			return f();
            """
        )
        assertEquals("valid",
            """
            A = null;
			{
				fun g() { return 'valid'; }
				fun test() { return g(); }
				A = test;
			}
			return A();
            """
        )
        assertEquals("1",
            """
             X = '1';
			fun test(X) {}
			test(100);
            return X;
            """
        )
        assertEquals("1",
            """
            fun f() {
				Y = '0';
				A = fun() { Y = '1'; };
				A();
				return Y;
			}
            return f();
            """
        )
        assertEquals("[1, 2, 3]",
            """
            fun f(args...) {
                return args;
            }
            return f(1, 2, 3).toString();
            """
        )
        throwsRuntime(
            """
            {
                fun a() {
                    return 10;
                }
            }
            return a();
            """
        )
    }

    @Test
    fun testReturnStatement() {
        assertEquals(10, "{ { { return 10; } } }")
        assertEquals(10, "return (fun() { return 10; })();")
        assertEquals(10, "while (true) { return 10; }")
    }

    @Test
    fun testWhileStatement() {
        throwsRuntime("while (null);")
        throwsRuntime("while (10);")
        assertEquals("1", "while (true) return '1';")
        assertEquals("1", "X = '0'; while (X == '0') { X = '1'; } return X;")
        assertEquals(10, "X = 0; while (X < 10) { X = X + 1; } return X;")
        assertEquals(2, "X = 0; while (X == 0) { while (true) { X = X + 1; break; } X = 2; } return X;")
        assertEquals(9, "while (false) return 10; return 9;")
        assertEquals(40,
            """
            i = 0;
			total = 0;
			while (i < 10) {
				if (i == 5) {
					i++;
					continue;
				}
				total = total + i;
				i++;
			}
			return total;
            """
        )
    }

    @Test
    fun testForStatement() {
        assertEquals(45,
            """
            total = 0;
			for (i = 0; i < 10; i++) {
				total = total + i;
			}
			return total;
            """
        )
        assertEquals(0,
            """
            total = 0;
			for (i = 0; i < 10; i++) {
				continue;
			}
			return total;    
            """
        )
        assertEquals(10,
            """
            fun call() {
				i++;
			}
			
			i = 0;
			for (; i < 10; call()) {
				continue;
			}
			return i;    
            """
        )
        assertEquals(0,
            """
			i = 0;
			for (; i < 10; ""()) {
				break;
			}
			return i;    
            """
        )
    }

    @Test
    fun testForEachStatement() {
        assertEquals(6,
            """
            fun doSomething() {
				return [1, 2, 3];
			}
			
			total = 0;
			foreach (thing : doSomething()) {
				total = total + thing;
			}
			return total;    
            """
        )
        assertEquals(45,
            """
            lazy = [];
            foreach (i : range(10)) {
                lazy.append(fun() {
                    return i;
                });
            }
            lazy[0] = lazy[0]();
            return lazy.reduce(fun(a, b) a + b());
            """
        )
        assertEquals(70,
            """
            t = 0;
			r = range(5, 10);
			foreach (i : r) {
				t = t + i;
			}
			foreach (i : r) {
				t = t + i;
			}
			return t;
            """
        )

        throwsRuntime("foreach (t : null);")
    }

    @Test
    fun testContinueStatement() {
        assertEquals("2", "X = '0'; while (X == '0') { X = '2'; continue; X = '4'; } return X;")
        assertEquals("3",
            """
            X = '0';
			while (X == '0') {
				X = '4';
				while (X == '4') {
					X = '2';
					continue;
				}
				if (X == '2') {
					X = '3';
					continue;
				}
			}
            return X;
            """
        )
    }

    @Test
    fun testBreakStatement() {
        assertEquals(1, "X = 0; while (true) { X = X + 1; break; } return X;")
        assertEquals(5,
            """
            i = 0;
            for (; i < 10; i++) {
                if (i == 5) {
                    break;
                }
            }
            return i;
            """
        )
    }

    @Test
    fun testTryStatement() {
        assertEquals("1", "X = '1'; try; catch (error); return X;")
        assertEquals("1",
            """
            X = '0';
			try {
				X = '1';
				throw null;
				X = '2';
			}
			catch (error) {
                return X;
            }
            """
        )
        assertEquals(1,
            """
            X = true;
			I = 0;
			while (I < 10) {
				try {
					X = X + 1;
				}
				catch (error) {
					X = -8;
				}
				I = I + 1;
			}
            return X;
            """
        )
        assertEquals("1",
            """
            X = null;
			I = 0;
			while (I < 10) {
				try {
					X = fun () {
						return '1';
					};
					if (I == 9) {
						X = null;
					}
					X();
				}
				catch (error) {
					X = '1';
				}
				I = I + 1;
			}
            return X;
            """
        )
        assertEquals(11,
            """
            x = 10;
            try;
            finally x = 11;
            return x;
            """
        )
        assertEquals(1,
            """
            x = 1;
            fun tryTest() {
                try {
                    throw null;
                } catch (e) {
                    return x;
                } finally {
                    x = 2;
                }
            }
            return tryTest();
            """
        )
        assertEquals(2,
            """
            x = 1;
            fun tryTest() {
                try {
                    return x;
                } finally {
                    x = 2;
                }
            }
            tryTest();
            return x;
            """
        )

        throwsRuntime(
            """
            try {
				if (true) {
					X = '1';
					throw null;
				}
			}
			catch (error) {
				print(X);
			}
            """
        )
    }

    @Test
    fun testThrowStatement() {
        throwsRuntime("throw null;")
        throwsRuntime("throw new Error();")
    }

    @Test
    fun testClassStatement() {
        // Operators
        assertEquals(-1,
            """
            class Test {
				var num = 1;
			
				operator ! () {
					return this.num * -1;
				}
			}
			return !new Test();    
            """
        )
        assertEquals(-10,
            """
			class Test {
				var num = 1;
			
				operator - (other) {
					this.num = 10;
					return this;
				}
				
				operator - () {
					return this.num * -1;
				}
			}
			return -(new Test() - 0);
			"""
        )
        assertEquals(30,
            """
			class Test {
				var num = 20;
			
				operator + (number) {
					return this.num + number;
				}
			}
			return new Test() + 10;
			"""
        )
        assertEquals(20,
            """
			class E {
				operator [] (accessor) {
					return 10;
				}
			}
			e = new E();
			return e[0] + e[e];
			"""
        )
        assertEquals(42,
            """
			class E {
				operator [] (index, value) {
					return index + value;
				}
			}
			e = new E();
			return e[10] = 32;
			"""
        )
        assertEquals("foobar",
            """
			class E {
				var A;
				
				operator [] (i, v) {
					this.A = i + v;
				}
			}
			e = new E();
			e["foo"], a, b = ["bar", 1, 2];
			return e.A;
			"""
        )
        // Functions
        assertEquals(true,
            """
			class Test {
				
			}
			test = new Test();
			return test == test;
			"""
        )
        assertEquals("test",
            """
			class Test {
				fun toString() {
					return 'test';
				}
			}
			return new Test().toString();
			"""
        )
        assertEquals("[test]",
            """
			class Test {
				fun toString() {
					return 'test';
				}
			}
			return [new Test()].toString();
			"""
        )
        assertEquals("[test]",
            """
			class Test {
				fun toString() {
					return 'test';
				}
			}
			return [new Test().toString()].toString();
			"""
        )
        assertEquals(1,
            """
			class Test {
				var num = 0;
				
				fun increment() {
					this.num++;
					return this.num;
				}
			}
			return new Test().increment();
			"""
        )
        assertEquals(11,
            """
			class Test {
				var num = 0;
				
				Test(start) {
					this.num = start;
				}
				
				fun increment() {
					this.num++;
					return this;
				}
			
				fun toString() {
					return this.num;
				}
			}
			return new Test(10).increment().toString();
			"""
        )
        assertEquals(3,
            """
			class Test {
				fun test() { 
                    return 1;
                }
				
				fun test(p) { 
                    return 2;
                }
			}
			testDelegate = new Test().test;
            return testDelegate() + testDelegate(null);
			"""
        )
        assertEquals(true,
            """
			class Test {
				fun test() {
					return true;
				}
			}
			testDelegate = new Test().test;
			return testDelegate();
			"""
        )
        throwsRuntime(
        """
			class Test {
				Test(p1) { }
			}
			new Test();
			"""
        )
        throwsRuntime(
        """
			class Test {
				fun test() {
				}
			}
			test = new Test();
			test.test = 10;
			"""
        )
        // Static
        assertEquals(3.14,
            """
			class Test {
				static var pi = 3.14;
			}
			return Test.pi;
			"""
        )
        assertEquals(2.72,
            """
			class Test {
				static var pi = 3.14;
				static var e = 2.72;
			}
			Test.pi = Test.e;
			return Test.pi;
			"""
        )
        assertEquals(3.14,
            """
			class Test {
				static var pi = 3.14;
			
				static fun pi() {
					return Test.pi;
				}
			}
			return Test.pi();
			"""
        )
        throwsRuntime(
        """
			class Test {
			
			}
			Test.test = 10;
			"""
        )
        // Arbitrary
        assertEquals("[1, 2, 3, 4]",
            """
			class E {
				fun test(a...) {
					return a;
				}

				static fun test(a...) {
					return a;
				}
			}

			return new E().test(1, 2).addAll(E.test(3, 4)).toString();
			"""
        )
        assertEquals("E",
            """
			class E {
				fun test(a...) {
					return this.toString();
				}

				fun toString() {
					return "E";
				}
			}

			return new E().test();
			"""
        )
        assertEquals(0,
            """
			class E {
				var total = 0;
				
				E(params...) {
					foreach (param : params) {
						this.total = this.total + param;
					}
				}
			}
			
			return new E(-1, 4, -3).total;
			"""
        )
        assertEquals(0,
            """
			class E {
				var total = 0;
				
				E(params...) {
					foreach (param : params) {
						this.total = this.total + param;
					}
				}
			}
			
			return new E().total;
			"""
        )
        // Delegates
        assertEquals(10,
            """
			class E {
				var func = fun() {
					return 10;
				};
			}
			return new E().func();
			"""
        )
        assertEquals(10,
            """
			class E {
				fun get10() {
					return 10;
				}
			}
			del = new E().get10;
			return del();
			"""
        )
        assertEquals(12,
            """
			class E {
				var e;
				fun getVal() {
					return this.e;
				}
			}
			e = new E();
			e.e = 11;
			del = e.getVal;
			e.e = 12;
			return del();
			"""
        )
        assertEquals("E@",
            """
			class E {
				
			}
			del = new E().toString;
			return del().subString(0, 2);
			"""
        )
        assertEquals(10,
            """
			class E {
				static fun get10() {
					return 10;
				}
			}
			del = E.get10;
			return del();
			"""
        )
    }

    @Test
    fun testInterfaceStatement() {
        assertEquals(10,
            """
            interface A { 
                fun get(): Number;
            }
            class B: A {
                fun get() return 10;
            }
            fun get(gettable: A) {
                return gettable.get();
            }
            return get(new B());
            """
        )
        assertEquals(10,
            """
            interface A { }
            class B: A { 
                fun get10(): Number return 10;
            }
            return new B().get10();
            """
        )
        assertEquals("wowwow",
            """
            interface A {
                fun conflict(): String;
            }
            interface B {
                fun conflict(): String;
            }
            class C: A, B {
                fun conflict() {
                    return "wow";
                }
            }
            fun a(x: A) return x.conflict();
            fun b(x: B) return x.conflict();
            c = new C();
            return a(c) + b(c);
            """
        )
        throwsRuntime(
            """
            interface A {
                fun something();
            }
            class B: A {
            }
            """
        )
    }

    @Test
    fun testEnumStatement() {
        throwsRuntime("enum E { A } E.A = '';")
        assertEquals(10,
            """
            enum E {
                A;
                static var T;
            }
               
            E.T = 10;
            return E.T;
            """
        )
        assertEquals("[1, 2, 3]",
            """
			enum E {
				A(1, 2, 3),
				B;
				
				var values;
				E(a...) {
					this.values = a;
				}
			}
			   
			return E.A.values.addAll(E.B.values).toString();
			"""
        )
        assertEquals("a",
            """
            enum E {
                A;
                static var A = 10;
                
                fun toString() {
                    return "a";
                }
            }
            return E.A.toString();
            """
        )
    }

    @Test
    fun testImportStatement() {
        assertEquals(false,
            """
            import Java from util.Internal;
            return Java.valueOf("name").isBlank();
            """
        )
        assertEquals(false,
            """
            import Java, JavaClass from util.Internal;
            return Java.type == JavaClass.type;
            """
        )
        assertEquals(false,
            """
            import * from util.Internal;
            return Java.type == JavaClass.type;
            """
        )
        throwsRuntime("import NonExistent from not.real.Import;")
        throwsRuntime("Java.valueOf('name').isBlank();")
    }
}