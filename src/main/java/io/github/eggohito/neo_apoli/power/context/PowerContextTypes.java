package io.github.eggohito.neo_apoli.power.context;

import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.util.context.ContextType;

public class PowerContextTypes {

	public static final ContextType GENERIC = new ContextType.Builder()
		.require(ContextParameters.POSITION)
		.require(ContextParameters.THIS_ENTITY)
		.build();

	public static final ContextType BLOCK = new ContextType.Builder()
		.require(ContextParameters.POSITION)
		.require(ContextParameters.THIS_ENTITY)
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY)
		.allow(ContextParameters.DIRECTION)
		.build();

}
