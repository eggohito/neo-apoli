package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextType;

public class ContextTypes {

	public static final ContextType GENERIC = new ContextType.Builder()
		.require(ContextParameters.THIS_ENTITY)
		.require(ContextParameters.POSITION)
		.allow(ContextParameters.POWER_REFERENCE)
		.build();

	public static final ContextType BIENTITY = new ContextType.Builder()
		.allow(ContextParameters.ACTOR)
		.allow(ContextParameters.TARGET)
		.allow(ContextParameters.POWER_REFERENCE)
		.build();

	public static final ContextType ITEM = new ContextType.Builder()
		.require(ContextParameters.STACK_REFERENCE)
		.require(ContextParameters.ITEM_STACK)
		.allow(ContextParameters.POWER_REFERENCE)
		.allow(ContextParameters.HAND)
		.build();

	public static final ContextType BLOCK = new ContextType.Builder()
		.require(ContextParameters.POSITION)
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY)
		.allow(ContextParameters.DIRECTION)
		.build();

}
