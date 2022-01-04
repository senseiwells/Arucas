package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

import java.util.*;

/**
 * A function map that provides a <code>O(1)</code> lookup time complexity for class definitions.
 */
public class ArucasClassDefinitionMap implements Iterable<AbstractClassDefinition> {
	private final Map<Class<?>, Set<AbstractClassDefinition>> classMap;
	private final Map<String, AbstractClassDefinition> nameMap;
	
	public ArucasClassDefinitionMap() {
		this.classMap = new HashMap<>();
		this.nameMap = new HashMap<>();
	}
	
	/**
	 * This method adds the class definition value to all of its subclasses
	 */
	private void addSubclasses(AbstractClassDefinition value) {
		Class<?> clazz = value.getValueClass();
		Set<AbstractClassDefinition> baseSet = this.classMap.computeIfAbsent(clazz, (v) -> new HashSet<>());
		baseSet.add(value);
		
		while (clazz != null && clazz != Object.class) {
			clazz = clazz.getSuperclass();
			
			// Get the subclass list
			Set<AbstractClassDefinition> set = this.classMap.get(clazz);
			if (set != null) {
				// Add all the subclass entries to the base class
				baseSet.addAll(set);
			}
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
		
		// Adding the class here is a bit different because we need to make sure all subclass
		// values also belong to this class
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
	public Set<AbstractClassDefinition> get(Class<?> clazz) {
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
