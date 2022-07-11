package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.TypeValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractClassDefinition implements MemberOperations {
	private final String name;
	private final ArucasFunctionMap<FunctionValue> staticMethods;
	private final Map<String, Value> staticMemberVariables;
	private final TypeValue typeValue;
	private Context localContext;

	public AbstractClassDefinition(String name) {
		this.name = name;
		this.staticMethods = new ArucasFunctionMap<>();
		this.staticMemberVariables = new LinkedHashMap<>();
		this.typeValue = new TypeValue(this);
	}

	public final String getName() {
		return this.name;
	}

	public abstract ArucasFunctionMap<? extends FunctionValue> getMethods();

	public abstract ArucasFunctionMap<? extends FunctionValue> getConstructors();

	public final Map<String, Value> getStaticMemberVariables() {
		return this.staticMemberVariables;
	}

	public final ArucasFunctionMap<FunctionValue> getStaticMethods() {
		return this.staticMethods;
	}

	public final TypeValue getType() {
		return this.typeValue;
	}

	public final void addStaticMethod(FunctionValue method) {
		this.staticMethods.add(method);
	}

	public final void init(Context context) throws ThrowValue, CodeError {
		this.localContext = context.createBranch();
		this.initialiseStatics(context);
	}

	public final Context getLocalContext(Context fallback) {
		return this.localContext == null ? fallback : this.localContext.createBranch();
	}

	public Class<?> getDefiningClass() {
		return this.getClass();
	}

	/**
	 * Returns the value type of this class.
	 */
	public abstract Class<? extends Value> getValueClass();

	/**
	 * Returns whether the class instance has a field with name
	 */
	public abstract boolean hasMemberField(String name);

	/**
	 * This gets called when the class is initialised.
	 */
	protected abstract void initialiseStatics(Context context) throws CodeError, ThrowValue;

	/**
	 * This gets called when a new instance of this object is created.
	 */
	public abstract Value createNewDefinition(Context context, List<Value> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue;

	/**
	 * These methods are for Runtime, when accessing and assigning static members.
	 */

	@Override
	public boolean isAssignable(String name) {
		// Only static member variables are modifiable
		return this.staticMemberVariables.get(name) != null;
	}

	@Override
	public final boolean hasMember(String name) {
		return this.getMember(name) != null;
	}

	@Override
	public final boolean hasMember(String name, int parameters) {
		return this.staticMethods.has(name, parameters);
	}

	@Override
	public final FunctionValue getDelegate(String name) {
		return this.staticMethods.get(name);
	}

	@Override
	public final FunctionValue getMember(String name, int parameters) {
		return this.staticMethods.get(name, parameters);
	}

	@Override
	public boolean setMember(String name, Value value) {
		if (!this.isAssignable(name)) {
			return false;
		}

		this.staticMemberVariables.put(name, value);
		return true;
	}

	@Override
	public Value getMember(String name) {
		if (name.equals("type")) {
			return this.getType();
		}
		Value member = this.staticMemberVariables.get(name);
		return member == null ? this.staticMethods.get(name) : member;
	}

	@Override
	public final ArucasFunctionMap<?> getAllMembers() {
		return this.staticMethods;
	}
}
