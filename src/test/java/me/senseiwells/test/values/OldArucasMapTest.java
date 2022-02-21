package me.senseiwells.test.values;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.OldArucasMap;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.test.ArucasHelper;
import org.junit.Test;

import static org.junit.Assert.*;

@Deprecated
public class OldArucasMapTest {
	@Test
	public void mapPutTest() throws CodeError  {
		Context context = ArucasHelper.createContext();
		OldArucasMap map = new OldArucasMap();
		
		assertEquals(map.size(), 0);
		map.put(context, StringValue.of("A"), NullValue.NULL);
		map.put(context, StringValue.of("A"), NumberValue.of(10));
		
		assertNotEquals(map.get(context, StringValue.of("A")), NullValue.NULL);
		assertTrue(map.containsKey(context, StringValue.of("A")));
		assertEquals(map.size(), 1);
	}
	
	@Test
	public void mapClearTest() throws CodeError {
		Context context = ArucasHelper.createContext();
		OldArucasMap map = new OldArucasMap();
		
		for (int i = 0; i < 10000; i++) {
			map.put(context, StringValue.of(Integer.toString(i)), NullValue.NULL);
			assertEquals(map.size(), i + 1);
		}
		
		map.clear();
		assertEquals(map.size(), 0);
	}
	
	@Test
	public void mapRemoveTest() throws CodeError {
		Context context = ArucasHelper.createContext();
		OldArucasMap map = new OldArucasMap();
		
		for (int i = 0; i < 10000; i++) {
			map.put(context, StringValue.of(Integer.toString(i)), NullValue.NULL);
			assertEquals(map.size(), i + 1);
		}
		
		for (int i = 0; i < 10000; i++) {
			map.remove(context, StringValue.of(Integer.toString(i)));
			assertEquals(map.size(), 9999 - i);
		}
		
		assertEquals(map.size(), 0);
	}
}
