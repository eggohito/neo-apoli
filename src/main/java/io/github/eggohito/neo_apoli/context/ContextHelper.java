package io.github.eggohito.neo_apoli.context;

import com.google.common.collect.Sets;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;

public class ContextHelper {

	public static ContextKeySet mergeKeySets(ContextKeySet first, ContextKeySet second) {

		ContextKeySet.Builder builder = new ContextKeySet.Builder();

		Set<ContextKey<?>> required = Sets.union(first.required(), second.required());
		Set<ContextKey<?>> allowed = Util.make(new ObjectOpenHashSet<>(Sets.union(first.allowed(), second.allowed())), params -> params.removeAll(required));

		required.forEach(builder::required);
		allowed.forEach(builder::optional);

		return builder.build();

	}

	public static ContextKeySet mergeKeySets(ContextKeySet... keySets) {
		return Arrays.stream(keySets)
			.reduce(ContextHelper::mergeKeySets)
			.orElseThrow(() -> new IllegalArgumentException("Couldn't merge without context key sets!"));
	}

	public static <U extends ContextUser> void validateAll(List<U> users, Context.Validator validator, IntFunction<String> pathFunction) {
		MiscUtil.iterateList(users, (index, user) -> user.validate(validator.forChild(pathFunction.apply(index))));
	}

}
