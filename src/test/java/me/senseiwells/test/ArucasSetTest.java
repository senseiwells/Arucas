package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasSetTest {
	@Test
	public void testSetOrder() {
		assertEquals("<1, 3, 4, 5>", ArucasHelper.runSafe(
			"""
			S = Set.of(1, 2, 3, 4);
			S.add(5);
			S.remove(2);
			return S;
			"""
		));
		assertEquals("<1, 2, 3>", ArucasHelper.runSafe(
			"""
			S = Set.of(1, 2, 3);
			S.add(1);
			return S;
			"""
		));
	}

	@Test
	public void testSetGet() {
		assertEquals("ten", ArucasHelper.runSafe(
			"""
			class A {
				var value;
				var other;
				
				A (value, other) {
					this.value = value;
					this.other = other;
				}
				
				fun hashCode() {
					return this.value;
				}
				
				operator == (other) {
					return other.instanceOf("A") && other.value == this.value;
				}
				
				fun toString() {
					return this.other;
				}
			}
			
			S = Set.of();
			S.add(new A(10, "ten"));
			return S.get(new A(10, null));
			"""
		));
	}
}
