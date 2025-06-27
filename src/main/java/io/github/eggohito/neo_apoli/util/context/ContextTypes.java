package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextType;

public class ContextTypes {

	public static final ContextType GENERIC = builder()
		.build();

	public static final ContextType GENERIC_WITH_ITEM = builder()
		.require(ContextParameters.STACK_REFERENCE)
		.require(ContextParameters.ITEM_STACK)
		.allow(ContextParameters.HAND)
		.build();

	public static final ContextType BLOCK = builder()
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY)
		.allow(ContextParameters.DIRECTION)
		.build();

	public static final ContextType BLOCK_WITH_ITEM = builder()
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY)
		.allow(ContextParameters.DIRECTION)
		.require(ContextParameters.STACK_REFERENCE)
		.require(ContextParameters.ITEM_STACK)
		.allow(ContextParameters.HAND)
		.build();

	public static ContextType.Builder builder() {
		return new ContextType.Builder()
			.allow(ContextParameters.POWER_REFERENCE)
			.require(ContextParameters.THIS_ENTITY)
			.require(ContextParameters.POSITION)
			.allow(ContextParameters.ACTOR)
			.allow(ContextParameters.TARGET);
	}

}
