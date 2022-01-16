package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.MergedClassMethods;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.*;

/**
 * A function map that provides a <code>O(n)</code> lookup time complexity for class definitions.
 */
public class ArucasClassDefinitionMap implements Iterable<AbstractClassDefinition> {
	private final Map<Class<?>, List<AbstractClassDefinition>> classMap;
	private final Map<Class<?>, MergedClassMethods> mergedClassMap;
	private final Map<String, AbstractClassDefinition> nameMap;
	
	public ArucasClassDefinitionMap() {
		this.classMap = new HashMap<>();
		this.mergedClassMap = new HashMap<>();
		this.nameMap = new HashMap<>();
	}

	/**
	 * Returns the index of the class in the specified list.
	 */
	private int getClassIndex(Class<?> clazz, List<AbstractClassDefinition> list) {
		final int length = list.size();

		for (int i = 0; i < length; i++) {
			if (list.get(i).getValueClass().isAssignableFrom(clazz)) {
				return i;
			}
		}

		return length;
	}
	
	/**
	 * This method adds the class definition value to all of its subclasses.
	 */
	private void addSubclasses(AbstractClassDefinition value) {
		Class<?> clazz = value.getValueClass();
		Class<?> baseClazz = clazz;
		List<AbstractClassDefinition> baseSet = this.classMap.computeIfAbsent(clazz, (v) -> new ArrayList<>());
		boolean isEmpty = baseSet.isEmpty();
		baseSet.add(0, value);

		if (clazz != null) {
			if (isEmpty) {
				while (clazz != null && clazz != Object.class) {
					clazz = clazz.getSuperclass();

					List<AbstractClassDefinition> childList = this.classMap.get(clazz);
					if (childList != null) {
						baseSet.addAll(childList);
						break;
					}
				}
			}

			for (Class<?> key : this.classMap.keySet()) {
				if (key != null && baseClazz.isAssignableFrom(key) && baseClazz != key) {
					List<AbstractClassDefinition> childList = this.classMap.get(key);

					if (childList != null) {
						int classIndex = getClassIndex(baseClazz, childList);
						childList.add(classIndex, value);
					}
				}
			}
		}
	}
	
	/**
	 * Add all the values from the specified map without doing any hierarchy checks.
	 */
	public void insertAll(ArucasClassDefinitionMap map) {
		for (Class<?> key : map.classMap.keySet()) {
			this.classMap.put(key, map.classMap.get(key));
		}

		for (Class<?> key : map.mergedClassMap.keySet()) {
			this.mergedClassMap.put(key, map.mergedClassMap.get(key));
		}
		
		for (String key : map.nameMap.keySet()) {
			this.nameMap.put(key, map.nameMap.get(key));
		}
	}

	public void merge() {
		for (Class<?> key : this.classMap.keySet().toArray(Class<?>[]::new)) {
			this.mergedClassMap.put(key, MergedClassMethods.mergeMethods(this.classMap.get(key)));
			if (key != Value.class) {
				this.classMap.remove(key);
			}
		}
	}

	private void debug(Class<?> added, AbstractClassDefinition value) {
		System.out.printf("Adding: %s, %s\n", added, value);
		for (Class<?> key : this.mergedClassMap.keySet()) {
			System.out.printf("  (%s.class)\n    ", key == null ? "null" : key.getSimpleName());
			MergedClassMethods methods = this.mergedClassMap.get(key);
			System.out.printf("%s@%x, ", key == null ? "null" : (key.getSimpleName()), methods.hashCode());
			System.out.print("\n\n");
		}
	}

	public void add(AbstractClassDefinition value) {
		this.nameMap.putIfAbsent(value.getName(), value);
		this.addSubclasses(value);
	}
	
	/**
	 * Returns true if this map is empty.
	 */
	public boolean isEmpty() {
		return this.classMap.isEmpty();
	}

	/**
	 * Returns the class definitions defined of the specified class.
	 */
	public FunctionValue getFunctionForClass(Class<?> clazz, String name, int parameters) {
		MergedClassMethods mergedMethods = this.mergedClassMap.get(clazz);
		if (mergedMethods != null) {
			return mergedMethods.getMethod(name, parameters);
		}

		for (AbstractClassDefinition classDefinition : this.classMap.get(clazz)) {
			FunctionValue functionValue = classDefinition.getMethods().get(name, parameters);
			if (functionValue != null) {
				return functionValue;
			}
		}

		return null;
	}
	
	/**
	 * Returns the class definition of the specified name.
	 */
	public AbstractClassDefinition get(String name) {
		return this.nameMap.get(name);
	}
	
	/**
	 * Returns true if this map contains the specified name.
	 */
	public boolean has(String name) {
		return this.nameMap.containsKey(name);
	}
	
	@Override
	public Iterator<AbstractClassDefinition> iterator() {
		// Create a non thread safe iterator of the current map
		return this.nameMap.values().iterator();
	}
}
