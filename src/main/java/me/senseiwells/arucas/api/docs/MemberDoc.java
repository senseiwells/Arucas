package me.senseiwells.arucas.api.docs;

import java.lang.annotation.*;

@Repeatable(MemberDoc.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MemberDoc {
	boolean isStatic() default false;
	String name();
	String desc();
	String type();
	boolean assignable() default false;
	String[] examples();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface List {
		MemberDoc[] value();
	}
}
