package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextType;

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

	public static final ContextType DAMAGE = new ContextType.Builder()
		.require(ContextParameters.DAMAGE_SOURCE)
		.allow(ContextParameters.DAMAGE_AMOUNT)
		.build();

	public static final ContextType ENTITY = new ContextType.Builder()
		.require(ContextParameters.ENTITY)
		.require(ContextParameters.ENTITY_POS)
		.build();

	public static final ContextType ITEM = new ContextType.Builder()
		.allow(ContextParameters.STACK_REFERENCE)
		.allow(ContextParameters.ITEM_STACK)
		.build();

}
