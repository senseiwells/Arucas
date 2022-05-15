package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.extensions.util.JavaValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValueConverter {
	final Map<Class<?>, Functions.Bi<?, Context, Value>> converterMap;
	Functions.Bi<Object[], Context, Value> arrayConverter;

	public ValueConverter() {
		this.converterMap = new LinkedHashMap<>();
	}

	/**
	 * This lets you add a Java class to be converted to a value. <br>
	 * The object that is passed into the function is
	 * guaranteed to be of the same type as clazz
	 */
	public <T> void addClass(Class<T> clazz, Functions.Bi<T, Context, Value> converter) {
		this.converterMap.put(clazz, converter);
	}

	/**
	 * This allows you to add a converter for all arrays, since you cannot
	 * check for primitives and Object arrays with a class check. So we
	 * have a function to box all primitive arrays to Object arrays
	 */
	public void addArrayConversion(Functions.Bi<Object[], Context, Value> converter) {
		this.arrayConverter = converter;
	}

	/**
	 * Converts a Java object into a Value<?> that is usable for
	 * Arucas. It searches for a suitable class match in the map,
	 * if it is unable to find one then it simply just returns a JavaValue
	 */
	public <T extends S, S> Value convertFrom(final T object, Context context) throws CodeError {
		if (object == null) {
			return NullValue.NULL;
		}
		if (object instanceof Value value) {
			return value;
		}

		Class<?> baseClazz = object.getClass();
		Class<?> clazz = baseClazz;
		while (clazz != Object.class) {
			@SuppressWarnings("unchecked")
			Functions.Bi<S, Context, Value> converter = (Functions.Bi<S, Context, Value>) this.converterMap.get(clazz);
			if (converter != null) {
				return converter.apply(object, context);
			}
			clazz = clazz.getSuperclass();
		}
		for (Class<?> iClass : baseClazz.getInterfaces()) {
			@SuppressWarnings("unchecked")
			Functions.Bi<S, Context, Value> converter = (Functions.Bi<S, Context, Value>) this.converterMap.get(iClass);
			if (converter != null) {
				return converter.apply(object, context);
			}
		}

		if (this.arrayConverter != null && baseClazz.isArray()) {
			return this.arrayConverter.apply(this.boxPrimitiveArray(object), context);
		}

		return JavaValue.of(object);
	}

	/**
	 * This is not a very 'clean' way of converting it, but primitive
	 * arrays do not count as Object[] so we must check for every
	 * array type and convert it to Object[] by boxing it.
	 */
	private Object[] boxPrimitiveArray(Object object) {
		if (object instanceof Object[] objects) {
			return objects;
		}
		if (object instanceof double[] doubles) {
			Double[] objects = new Double[doubles.length];
			for (int i = 0; i < doubles.length; i++) {
				objects[i] = doubles[i];
			}
			return objects;
		}
		if (object instanceof long[] longs) {
			Long[] objects = new Long[longs.length];
			for (int i = 0; i < longs.length; i++) {
				objects[i] = longs[i];
			}
			return objects;
		}
		if (object instanceof int[] ints) {
			Integer[] objects = new Integer[ints.length];
			for (int i = 0; i < ints.length; i++) {
				objects[i] = ints[i];
			}
			return objects;
		}
		if (object instanceof short[] shorts) {
			Short[] objects = new Short[shorts.length];
			for (int i = 0; i < shorts.length; i++) {
				objects[i] = shorts[i];
			}
			return objects;
		}
		if (object instanceof byte[] bytes) {
			Byte[] objects = new Byte[bytes.length];
			for (int i = 0; i < bytes.length; i++) {
				objects[i] = bytes[i];
			}
			return objects;
		}
		if (object instanceof float[] floats) {
			Float[] objects = new Float[floats.length];
			for (int i = 0; i < floats.length; i++) {
				objects[i] = floats[i];
			}
			return objects;
		}
		if (object instanceof boolean[] bools) {
			Boolean[] objects = new Boolean[bools.length];
			for (int i = 0; i < bools.length; i++) {
				objects[i] = bools[i];
			}
			return objects;
		}
		if (object instanceof char[] chars) {
			Character[] objects = new Character[chars.length];
			for (int i = 0; i < chars.length; i++) {
				objects[i] = chars[i];
			}
			return objects;
		}
		throw new IllegalArgumentException("%s is not an array".formatted(object));
	}
}
