package me.senseiwells.arucas.api.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FunctionDoc {
	boolean isVarArgs() default false;
	boolean isStatic() default false;
	String[] deprecated() default { };
	String name();
	String[] desc();
	String[] params() default { };
	String[] returns() default { };
	String[] throwMsgs() default { };
	String[] example();
}
