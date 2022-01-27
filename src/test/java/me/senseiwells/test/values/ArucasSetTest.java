package me.senseiwells.test.values;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.test.ArucasHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArucasSetTest {
	@Test
	public void setAddSimpleTest() throws CodeError  {
		Context context = ArucasHelper.createContext();
		ArucasSet set = new ArucasSet();
		
		assertEquals(set.size(), 0);
		set.add(context, StringValue.of("A"));
		set.add(context, StringValue.of("A"));
		
		assertTrue(set.contains(context, StringValue.of("A")));
		assertEquals(set.size(), 1);
	}
	
	@Test
	public void setClearTest() throws CodeError {
		Context context = ArucasHelper.createContext();
		ArucasSet set = new ArucasSet();
		
		for (int i = 0; i < 10000; i++) {
			set.add(context, StringValue.of(Integer.toString(i)));
			assertEquals(set.size(), i + 1);
		}
		
		set.clear();
		assertEquals(set.size(), 0);
	}
	
	@Test
	public void setRemoveTest() throws CodeError {
		Context context = ArucasHelper.createContext();
		ArucasSet set = new ArucasSet();
		
		for (int i = 0; i < 10000; i++) {
			set.add(context, StringValue.of(Integer.toString(i)));
			assertEquals(set.size(), i + 1);
		}
		
		for (int i = 0; i < 10000; i++) {
			set.remove(context, StringValue.of(Integer.toString(i)));
			assertEquals(set.size(), 9999 - i);
		}
		
		assertEquals(set.size(), 0);
	}
}
