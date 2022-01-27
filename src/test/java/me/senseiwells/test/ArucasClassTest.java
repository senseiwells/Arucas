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
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test { } Test();"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class Test() { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("classValue = class Test { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class String { }"));
		assertEquals("null", ArucasHelper.runSafe(
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
			}
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
	}

	@Test
	public void testClassFunction() {
		assertEquals("true", ArucasHelper.runSafeFull(
				"""
				class Test {
					
				}
				test = new Test();
				X = test.equals(test);
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
}
