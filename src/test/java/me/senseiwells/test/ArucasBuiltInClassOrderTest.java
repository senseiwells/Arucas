package me.senseiwells.test;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.impl.values.TestOrderValues;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArucasBuiltInClassOrderTest {
	@Test(timeout = 1000)
	public void debug() {
		// Create all permutations
		final List<Supplier<ArucasClassExtension>> suppliers = Arrays.asList(
			TestOrderValues.AValue.ClassExt::new,
			TestOrderValues.BValue.ClassExt::new,
			TestOrderValues.DValue.ClassExt::new
		);

		permutations(null, 3, (list) -> {
			ContextBuilder contextBuilder = new ContextBuilder()
				.setDisplayName("System.in")
				.addDefault();

			for (int i : list) {
				contextBuilder.addBuiltInClasses(suppliers.get(i));
			}

			Context context = contextBuilder.build();

			Assert.assertEquals("A: (A), B: (B), D: (D)", ArucasHelper.runSafeFull(
				"""
				X = 'A: ' + new A().test() + ', B: ' + new B().test() + ', D: ' + new D().tes2();
				""", "X", context
			));
			Assert.assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafeFull(
				"""
				new A().tes2();
				""", "", context
			));
		});
	}

	private static void permutations(LinkedList<Integer> list, int count, Consumer<LinkedList<Integer>> consumer) {
		if (list == null) {
			list = new LinkedList<>();
		}

		if (list.size() == count) {
			consumer.accept(list);
			return;
		}

		for (int i = 0; i < count; i++) {
			if (!list.contains(i)) {
				list.addLast(i);
				permutations(list, count, consumer);
				list.pollLast();
			}
		}
	}
}
