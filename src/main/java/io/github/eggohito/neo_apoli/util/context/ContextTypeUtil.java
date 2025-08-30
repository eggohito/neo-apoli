package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;

import java.util.Arrays;
import java.util.Set;

public class ContextTypeUtil {

	/**
	 * 	<p>Constructs a {@link ContextType.Builder} from a {@link ContextType}; useful in cases where you want to add
	 * 	onto a context type.</p>
	 * 	@param type the context type to convert into a builder
	 * 	@return a context type builder
	 */
	public static ContextType.Builder toBuilder(ContextType type) {

		ContextType.Builder builder = new ContextType.Builder();
		type.getRequired().forEach(builder::require);

		Set<ContextParameter<?>> allowedParameters = Util.make(new ObjectOpenHashSet<>(type.getAllowed()), params -> params.removeAll(type.getRequired()));
		allowedParameters.forEach(builder::allow);

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
	public static ContextType merge(ContextType first, ContextType second) {

		ContextType.Builder builder = new ContextType.Builder();

		Set<ContextParameter<?>> requiredParameters = Sets.union(first.getRequired(), second.getRequired());
		Set<ContextParameter<?>> allowedParameters = Util.make(new ObjectOpenHashSet<>(Sets.union(first.getAllowed(), second.getAllowed())), parameters -> parameters.removeAll(requiredParameters));

		requiredParameters.forEach(builder::require);
		allowedParameters.forEach(builder::allow);

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
	public static ContextType merge(ContextType... contextTypes) {
		return Arrays.stream(contextTypes)
			.reduce(ContextTypeUtil::merge)
			.orElseThrow(() -> new IllegalArgumentException("Couldn't merge context parameters without context types!"));
	}

}
