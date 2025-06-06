package io.github.eggohito.neo_apoli.power.context;

import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.util.context.ContextType;

public class PowerContextTypes {

	public static final ContextType.Builder BUILDER = new ContextType.Builder()
		.require(ContextParameters.POSITION)
		.require(ContextParameters.THIS_ENTITY)
		.allow(ContextParameters.ACTOR)
		.allow(ContextParameters.TARGET)
		.allow(ContextParameters.ITEM_STACK);

	public static final ContextType GENERIC = BUILDER
		.build();

	public static final ContextType BLOCK = BUILDER
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY)
		.allow(ContextParameters.DIRECTION)
		.build();

}
