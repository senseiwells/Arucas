package me.senseiwells.arucas.api;

import me.senseiwells.arucas.utils.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Runtime context class of the programming language
 */
public class ContextBuilder {
	private List<Class<? extends IArucasExtension>> extensions = List.of();
	private String displayName = "";
	
	public ContextBuilder() {
	
	}
	
	public ContextBuilder setDisplayName(String displayName) {
		this.displayName = Objects.requireNonNull(displayName);
		return this;
	}

	@SuppressWarnings("unused")
	public ContextBuilder setExtensions(List<Class<? extends IArucasExtension>> extensions) {
		this.extensions = Objects.requireNonNull(extensions);
		return this;
	}
	
	@SafeVarargs
	public final ContextBuilder setExtensions(Class<? extends IArucasExtension>... extensions) {
		this.extensions = List.of(extensions);
		return this;
	}
	
	public Context create() {
		List<IArucasExtension> list = new ArrayList<>();
  
		for (Class<? extends IArucasExtension> clazz : this.extensions) {
			try {
				list.add(clazz.getDeclaredConstructor().newInstance());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return new Context(this.displayName, list);
	}
}
