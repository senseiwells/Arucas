package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasCastingTest {
	@Test
	public void syntaxTest() {
		assertEquals("fo", ArucasHelper.runSafe(
			"""
			class E {
				var string: String = "";
				
				as String {
					return this.string;
				}
			}
			
			e = new E();
			e.string = "foo";
			return e.subString(0, 2);
			"""
		));
		assertEquals(":D", ArucasHelper.runSafe(
			"""
			class E {
				var string: String = "";
				
				fun subString(a: Number, b: Number): String {
					return ":D";
				}
				
				as String {
					return this.string;
				}
			}
			
			e = new E();
			e.string = "foo";
			return e.subString(0, 2);
			"""
		));
		assertEquals("map", ArucasHelper.runSafe(
			"""
			class E {
				var map: Map = {0: "map"};
				var list: List = ["list"];
				
				as Map {
					return this.map;
				}
				
				as List {
					return this.list;
				}
			}
			
			e = new E();
			return e.get(0);
			"""
		));
		assertEquals("E", ArucasHelper.runSafe(
			"""
			class E {
				as String {
					return "";
				}
			}
			
			fun X(arg: E | String) {
				return Type.of(arg).getName();
			}
			return X(new E());
			"""
		));
		assertEquals("E", ArucasHelper.runSafe(
			"""
			class E {
				as String {
					return "";
				}
			}
			
			fun X(arg: String | E) {
				return Type.of(arg).getName();
			}
			return X(new E());
			"""
		));

		assertEquals("String", ArucasHelper.runSafe(
			"""
			class E { }
			class F {
				as E {
					return new E();
				}
				
				as String  {
					return "";
				}
			}
			
			fun X(arg: String | E) {
				return Type.of(arg).getName();
			}
			return X(new F());
			"""
		));
		assertEquals("E", ArucasHelper.runSafe(
			"""
			class E { }
			class F {
				as E {
					return new E();
				}
				
				as String  {
					return "";
				}
			}
			
			fun X(arg: E | String) {
				return Type.of(arg).getName();
			}
			return X(new F());
			"""
		));
	}
}
