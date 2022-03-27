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
	public void testEmbedding() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class E { embed E as e; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class E { embed Object as obj; embed Object as obj; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class E { embed Object; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class E { embed Object as; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("class E { embed var a; }"));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class E {
				embed String as s;
			}
			new E().lowercase();
			"""
		));
		assertEquals("E", ArucasHelper.runSafe(
			"""
			class E {
				embed Object as obj;
			}
			return Type.of(new E()).getName();
			"""
		));
		assertEquals("e test", ArucasHelper.runSafe(
			"""
			class E {
				embed String as s = "E tEsT";
			}
			return new E().lowercase();
			"""
		));
		assertEquals("99", ArucasHelper.runSafe(
			"""
			class E {
				embed Map as m = { "E" : 99 };
			}
			return new E().get("E");
			"""
		));
		assertEquals("true", ArucasHelper.runSafe(
			"""
			class E {
				embed Map as m = { "E" : 99 };
			}
			return Type.of(new E()).hasEmbed(Map.type);
			"""
		));
		assertEquals("false", ArucasHelper.runSafe(
			"""
			class E {
				embed Map as m = { "E" : 99 };
			}
			return Type.of(new E()).hasEmbed(Object.type);
			"""
		));
	}
}
