package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ArucasClassTest {
	@Test(timeout = 1000)
	public void testClassSyntax() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { print('test'); }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { static static; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { Tests() { } }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { if (true) { } }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { } new Test;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test() { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("classValue = class Test { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class String { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { var e; var e; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { static var e; static var e; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.runUnsafe("class Test { } Test();"));
		assertEquals("test", ArucasHelper.runSafe(
			"""
			class Test {
				static var staticMember;
				static var staticMemberInitialised = 10;
				
				static {
					Test.staticMember = Test.staticMemberInitialised;
				}
				
				Test() { }
				
				Test(param1) { }
				
				Test(param1, param2) { }
				
				operator + (other) { }
				
				operator ! () { }
			
				fun toString() {
					return 'test';
				}
				
				static fun getString() {
					return 'test';
				}
				
				static fun getString(param) { }
				
				static fun getString(param1, param2) { }
			}
			return Test.getString();
			"""
		));
	}

	@Test
	public void testClassOperator() {
		assertEquals("-1", ArucasHelper.runSafeFull(
			"""
			class Test {
				var num = 1;
			
				operator ! () {
					return this.num * -1;
				}
			}
			X = !new Test();
			""", "X"
		));
		assertEquals("-10", ArucasHelper.runSafeFull(
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
			X = -(new Test() - 0);
			""", "X"
		));
		assertEquals("30", ArucasHelper.runSafeFull(
			"""
			class Test {
				var num = 20;
			
				operator + (number) {
					return this.num + number;
				}
			}
			X = new Test() + 10;
			""", "X"
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class Test {
				operator ! (param) {
					return this;
				}
			}
			"""
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class Test {
				operator == () {
					return false;
				}
			}
			"""
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class Test {
				operator [] () { }
			}
			"""
		));

		assertEquals("20", ArucasHelper.runSafe(
			"""
			class E {
				operator [] (accessor) {
					return 10;
				}
			}
			e = new E();
			return e[0] + e[e];
			"""
		));
		assertEquals("42", ArucasHelper.runSafe(
			"""
			class E {
				operator [] (index, value) {
					return index + value;
				}
			}
			e = new E();
			return e[10] = 32;
			"""
		));
		assertEquals("foobar", ArucasHelper.runSafe(
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
		));
	}

	@Test
	public void testClassFunction() {
		assertEquals("true", ArucasHelper.runSafeFull(
			"""
			class Test {
				
			}
			test = new Test();
			X = test == test;
			""", "X"
		));
		assertEquals("test", ArucasHelper.runSafeFull(
			"""
			class Test {
				fun toString() {
					return 'test';
				}
			}
			X = new Test().toString();
			""", "X"
		));
		assertEquals("[test]", ArucasHelper.runSafeFull(
			"""
			class Test {
				fun toString() {
					return 'test';
				}
			}
			X = [new Test()];
			""", "X"
		));
		assertEquals("[\"test\"]", ArucasHelper.runSafeFull(
			"""
			class Test {
				fun toString() {
					return 'test';
				}
			}
			X = [new Test().toString()];
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			class Test {
				var num = 0;
				
				fun increment() {
					this.num++;
					return this.num;
				}
			}
			X = new Test().increment();
			""", "X"
		));
		assertEquals("11", ArucasHelper.runSafeFull(
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
			X = new Test(10).increment().toString();
			""", "X"
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class Test {
				Test(p1) { }
			}
			new Test();
			"""
		));
		assertEquals("true", ArucasHelper.runSafeFull(
			"""
			class Test {
				fun test() {
					return true;
				}
			}
			testDelegate = new Test().test;
			X = testDelegate();
			""", "X"
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class Test {
				fun test() { }
				
				fun test(p) { }
			}
			testDelegate = new Test().test;
			"""
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class Test {
				fun test() {
				}
			}
			test = new Test();
			test.test = 10;
			"""
		));
	}

	@Test
	public void testClassStatic() {
		assertEquals("3.14", ArucasHelper.runSafeFull(
			"""
			class Test {
				static var pi = 3.14;
			}
			X = Test.pi;
			""", "X"
		));
		assertEquals("2.72", ArucasHelper.runSafeFull(
			"""
			class Test {
				static var pi = 3.14;
				static var e = 2.72;
			}
			Test.pi = Test.e;
			X = Test.pi;
			""", "X"
		));
		assertEquals("3.14", ArucasHelper.runSafeFull(
			"""
			class Test {
				static var pi = 3.14;
			
				static fun pi() {
					return Test.pi;
				}
			}
			X = Test.pi();
			""", "X"
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class Test {
			
			}
			Test.test = 10;
			"""
		));
	}

	@Test
	public void testClassMethodDelegating() {
		assertEquals("10", ArucasHelper.runSafe(
			"""
			class E {
				var func = fun() {
					return 10;
				};
			}
			return new E().func();
			"""
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			class E {
				fun get10() {
					return 10;
				}
			}
			del = new E().get10;
			return del();
			"""
		));
		assertEquals("12", ArucasHelper.runSafe(
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
		));
		assertEquals("<class E", ArucasHelper.runSafe(
			"""
			class E {
				
			}
			del = new E().toString;
			return del().subString(0, 8);
			"""
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			class E {
				static fun get10() {
					return 10;
				}
			}
			del = E.get10;
			return del();
			"""
		));
	}

	@Test
	public void testArbitraryParameters() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class  E {
				fun test(a, b...) { }
			}
			"""
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class  E {
				fun test(a, b, c...) { }
			}
			"""
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class  E {
				fun test(...a) { }
			}
			"""
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class E {
				static fun test(a, b...) { }
			}
			"""
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class E {
				static fun test(...a) { }
			}
			"""
		));
		assertEquals("[1, 2, 3, 4]", ArucasHelper.runSafe(
			"""
			class E {
				fun test(a...) {
					return a;
				}

				static fun test(a...) {
					return a;
				}
			}

			return new E().test(1, 2).addAll(E.test(3, 4));
			"""
		));
		assertEquals("E", ArucasHelper.runSafe(
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
		));
		assertEquals("0", ArucasHelper.runSafe(
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
		));
		assertEquals("0", ArucasHelper.runSafe(
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
		));
	}
}
