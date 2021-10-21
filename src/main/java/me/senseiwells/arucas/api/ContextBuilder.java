package me.senseiwells.arucas.api;

import me.senseiwells.arucas.utils.Context;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Runtime context class of the programming language
 */
public class ContextBuilder {
	private Set<Class<? extends IArucasExtension>> extensions = Set.of();
	private String displayName = "";
	
	public ContextBuilder() {
	
	}
	
	public ContextBuilder setDisplayName(String displayName) {
		this.displayName = Objects.requireNonNull(displayName);
		return this;
	}
	
	public ContextBuilder setExtensions(Set<Class<? extends IArucasExtension>> extensions) {
		this.extensions = Objects.requireNonNull(extensions);
		return this;
	}
	
	public Context create() {
		Set<IArucasExtension> set = new HashSet<>();
		
		for (Class<? extends IArucasExtension> clazz : this.extensions) {
			try {
				set.add(clazz.getDeclaredConstructor().newInstance());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return new Context(this.displayName, set);
	}
}
