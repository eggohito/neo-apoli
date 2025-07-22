package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.Sets;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;

import java.util.Arrays;
import java.util.Set;

public class ContextTypes {

	public static final ContextType GENERIC = new ContextType.Builder()
		.allow(ContextParameters.POWER_REFERENCE)
		.allow(ContextParameters.POSITION)
		.allow(ContextParameters.HAND)
		.build();

	public static final ContextType BIENTITY = new ContextType.Builder()
		.allow(ContextParameters.ACTOR)
		.allow(ContextParameters.TARGET)
		.build();

	public static final ContextType BLOCK = new ContextType.Builder()
		.require(ContextParameters.BLOCK_POS)
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY)
		.allow(ContextParameters.DIRECTION)
		.build();

	public static final ContextType ENTITY = new ContextType.Builder()
		.require(ContextParameters.ENTITY)
		.require(ContextParameters.ENTITY_POS)
		.build();

	public static final ContextType ITEM = new ContextType.Builder()
		.allow(ContextParameters.STACK_REFERENCE)
		.allow(ContextParameters.ITEM_STACK)
		.build();

	/**
	 * 	<p>Merge the required/allowed context parameters of two context types. Generally useful in cases where a type
	 * 	may not inherently support the context parameters of another type, but can provide said context parameters.</p>
	 *
	 * 	<p>Examples:</p>
	 * 	<ul>
	 * 	    <li>An entity action that can execute and provide the context parameters required by an item action (entity
	 * 	    actions do not inherently support the context parameters of an item action.)</li>
	 * 	    <li>A number provider that can test bi-entity conditions for counting how many entities fulfill it and providing
	 * 	    the count.</li>
	 * 	</ul>
	 *
	 * 	@param first the first context type
	 * 	@param second the second context type
	 *	@return a new context type instance with the required/allowed context parameters of the first and second context types
	 */
	public static ContextType merge(ContextType first, ContextType second) {

		ContextType.Builder builder = new ContextType.Builder();

		Set<ContextParameter<?>> requiredParameters = Sets.union(first.getRequired(), second.getRequired());
		Set<ContextParameter<?>> allowedParameters = Sets.union(first.getAllowed(), second.getAllowed());

		requiredParameters.forEach(parameter -> MiscUtil.tryCatch(() -> builder.require(parameter), e -> {}));
		allowedParameters.forEach(parameter -> MiscUtil.tryCatch(() -> builder.allow(parameter), e -> {}));

		return builder.build();

	}

	/**
	 * 	<p>Merge the required/allowed context parameters of two context types. Generally useful in cases where a type
	 * 	may not inherently support the context parameters of another type, but can provide said context parameters.</p>
	 *
	 * 	<p>Examples:</p>
	 * 	<ul>
	 * 	    <li>An entity action that can execute and provide the context parameters required by an item action (entity
	 * 	    actions do not inherently support the context parameters of an item action.)</li>
	 * 	    <li>A number provider that can test bi-entity conditions for counting how many entities fulfill it and providing
	 * 	    the count.</li>
	 * 	</ul>
	 *
	 * 	@param contextTypes the context types to merge the required/allowed context parameters of
	 * 	@return a new context type instance with the required/allowed context parameters of all the passed context types
	 */
	public static ContextType merge(ContextType... contextTypes) {
		return Arrays.stream(contextTypes)
			.reduce(ContextTypes::merge)
			.orElseThrow(() -> new IllegalArgumentException("Couldn't merge context parameters without context types!"));
	}

}
