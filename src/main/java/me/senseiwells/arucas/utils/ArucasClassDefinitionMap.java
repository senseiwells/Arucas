package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

import java.util.*;

/**
 * A function map that provides a <code>O(n)</code> lookup time complexity for class definitions.
 */
public class ArucasClassDefinitionMap implements Iterable<AbstractClassDefinition> {
	// TODO: Find a way to combine class definitions to make this O(1)
	private final Map<Class<?>, List<AbstractClassDefinition>> classMap;
	private final Map<String, AbstractClassDefinition> nameMap;
	
	public ArucasClassDefinitionMap() {
		this.classMap = new HashMap<>();
		this.nameMap = new HashMap<>();
	}
	
	/**
	 * Returns the index of the class in the specified list
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
	 * This method adds the class definition value to all of its subclasses
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
	 * Add all the values from the specified map without doing any hierarchy checks
	 */
	public void insertAll(ArucasClassDefinitionMap map) {
		for (Class<?> key : map.classMap.keySet()) {
			this.classMap.put(key, new ArrayList<>(map.classMap.get(key)));
		}
		
		for (String key : map.nameMap.keySet()) {
			this.nameMap.put(key, map.nameMap.get(key));
		}
	}
	
	private void debug(Class<?> added, AbstractClassDefinition value) {
		System.out.printf("Adding: %s, %s\n", added, value);
		for (Class<?> key : this.classMap.keySet()) {
			System.out.printf("  (%s.class)\n    ", key == null ? "null" : key.getSimpleName());
			List<AbstractClassDefinition> list = this.classMap.get(key);
			for (AbstractClassDefinition def : list) {
				System.out.printf("%s::%s@%x, ", def.getName(), def.getValueClass() == null ? "null" : (def.getValueClass().getSimpleName()), def.hashCode());
			}
			System.out.print("\n\n");
		}
	}
	
	/**
	 * Returns true if the function did not exist
	 */
	public boolean add(AbstractClassDefinition value) {
		if (this.nameMap.containsKey(value.getName())) {
			return false;
		}
		
		this.nameMap.put(value.getName(), value);
		this.addSubclasses(value);
		return true;
	}
	
	/**
	 * Returns true if this map is empty
	 */
	public boolean isEmpty() {
		return this.classMap.isEmpty();
	}

	/**
	 * Returns the class definitions defined of the specified class
	 */
	public List<AbstractClassDefinition> get(Class<?> clazz) {
		return this.classMap.get(clazz);
	}
	
	/**
	 * Returns the class definition of the specified name
	 */
	public AbstractClassDefinition get(String name) {
		return this.nameMap.get(name);
	}
	
	/**
	 * Returns true if this map contains the specified name
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
