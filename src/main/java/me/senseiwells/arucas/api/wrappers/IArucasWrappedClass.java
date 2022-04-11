package me.senseiwells.arucas.api.wrappers;

public interface IArucasWrappedClass {
	default Object asJavaValue() {
		return this;
	}
}
