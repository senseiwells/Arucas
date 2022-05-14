package me.senseiwells.impl.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

public class TestOrderValues {
	public static class AValue extends GenericValue<String> {
		public AValue() {
			super(null);
		}

		@Override
		public AValue copy(Context context) {
			return this;
		}

		@Override
		public String getAsString(Context context) {
			return this.getClass().toString();
		}

		@Override
		public boolean isEquals(Context context, Value other) {
			return false;
		}

		@Override
		public String getTypeName() {
			return "A";
		}

		@Override
		public int getHashCode(Context context) {
			return 0;
		}

		public static class ClassExt extends ArucasClassExtension {
			public ClassExt() {
				super("A");
			}

			@Override
			public Class<AValue> getValueClass() {
				return AValue.class;
			}

			@Override
			public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
				return ArucasFunctionMap.of(new ConstructorFunction((a, b) -> new AValue()));
			}

			@Override
			public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
				return ArucasFunctionMap.of(new MemberFunction("test", (a, b) -> StringValue.of("(A)")));
			}
		}
	}

	public static class BValue extends AValue {
		@Override
		public BValue copy(Context context) {
			return this;
		}

		@Override
		public String getAsString(Context context) {
			return this.getClass().toString();
		}

		@Override
		public boolean isEquals(Context context, Value other) {
			return false;
		}

		@Override
		public int getHashCode(Context context) {
			return 0;
		}

		@Override
		public String getTypeName() {
			return "B";
		}

		public static class ClassExt extends ArucasClassExtension {
			public ClassExt() {
				super("B");
			}

			@Override
			public Class<BValue> getValueClass() {
				return BValue.class;
			}

			@Override
			public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
				return ArucasFunctionMap.of(new ConstructorFunction((a, b) -> new BValue()));
			}

			@Override
			public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
				return ArucasFunctionMap.of(
					new MemberFunction("test", (a, b) -> StringValue.of("(B)"))
				);
			}
		}
	}

	public static class CoolExtensionClassThatShouldBeAllowed extends BValue {

	}

	public static class DValue extends CoolExtensionClassThatShouldBeAllowed {
		@Override
		public DValue copy(Context context) {
			return this;
		}

		@Override
		public String getAsString(Context context) {
			return this.getClass().toString();
		}

		@Override
		public boolean isEquals(Context context, Value other) {
			return false;
		}

		@Override
		public int getHashCode(Context context) {
			return 0;
		}

		@Override
		public String getTypeName() {
			return "D";
		}

		public static class ClassExt extends ArucasClassExtension {
			public ClassExt() {
				super("D");
			}

			@Override
			public Class<DValue> getValueClass() {
				return DValue.class;
			}

			@Override
			public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
				return ArucasFunctionMap.of(new ConstructorFunction((a, b) -> new DValue()));
			}

			@Override
			public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
				return ArucasFunctionMap.of(
					new MemberFunction("tes2", (a, b) -> StringValue.of("(D)"))
				);
			}
		}
	}
}
