package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;

import java.util.Arrays;
import java.util.Set;

public class ContextKeySetHelper {

	/**
	 * 	<p>Constructs a {@link ContextKeySet.Builder} from a {@link ContextKeySet}; useful in cases where you want to add
	 * 	onto a context type.</p>
	 * 	@param type the context type to convert into a builder
	 * 	@return a context type builder
	 */
	public static ContextKeySet.Builder toBuilder(ContextKeySet type) {

		ContextKeySet.Builder builder = new ContextKeySet.Builder();
		type.required().forEach(builder::required);

		Set<ContextKey<?>> allowedParameters = Util.make(new ObjectOpenHashSet<>(type.allowed()), params -> params.removeAll(type.required()));
		allowedParameters.forEach(builder::optional);

		return builder;

	}

	/**
	 * <p>Merge the required/allowed context parameters of two context types. Generally useful in cases where a type
	 * may not inherently support the context parameters of another type, but can provide said context parameters.</p>
	 *
	 * <p>Examples:</p>
	 * <ul>
	 *     <li>An entity action that can execute and provide the context parameters required by an item action (entity
	 *     actions do not inherently support the context parameters of an item action.)</li>
	 *     <li>A number provider that can test bi-entity conditions for counting how many entities fulfill it and providing
	 *     the count.</li>
	 * </ul>
	 * <p>
	 * @param first the first context type
	 * @param second the second context type
	 * @return a new context type instance with the required/allowed context parameters of the first and second context types
	 */
	public static ContextKeySet merge(ContextKeySet first, ContextKeySet second) {

		ContextKeySet.Builder builder = new ContextKeySet.Builder();

		Set<ContextKey<?>> requiredParameters = Sets.union(first.required(), second.required());
		Set<ContextKey<?>> allowedParameters = Util.make(new ObjectOpenHashSet<>(Sets.union(first.allowed(), second.allowed())), parameters -> parameters.removeAll(requiredParameters));

		requiredParameters.forEach(builder::required);
		allowedParameters.forEach(builder::optional);

		return builder.build();

	}

	/**
	 * <p>Merge the required/allowed context parameters of two context types. Generally useful in cases where a type
	 * may not inherently support the context parameters of another type, but can provide said context parameters.</p>
	 *
	 * <p>Examples:</p>
	 * <ul>
	 *     <li>An entity action that can execute and provide the context parameters required by an item action (entity
	 *     actions do not inherently support the context parameters of an item action.)</li>
	 *     <li>A number provider that can test bi-entity conditions for counting how many entities fulfill it and providing
	 *     the count.</li>
	 * </ul>
	 * <p>
	 * @param contextTypes the context types to merge the required/allowed context parameters of
	 * @return a new context type instance with the required/allowed context parameters of all the passed context types
	 */
	public static ContextKeySet merge(ContextKeySet... contextTypes) {
		return Arrays.stream(contextTypes)
			.reduce(ContextKeySetHelper::merge)
			.orElseThrow(() -> new IllegalArgumentException("Couldn't merge context parameters without context types!"));
	}

}
