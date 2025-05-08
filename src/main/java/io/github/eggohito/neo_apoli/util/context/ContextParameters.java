package io.github.eggohito.neo_apoli.util.context;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

public final class ContextParameters {

	public static final ContextParameter<Entity> CURRENT_ENTITY = new ContextParameter<>(NeoApoli.id("current_entity"));
	public static final ContextParameter<Vec3d> POSITION = new ContextParameter<>(NeoApoli.id("position"));

	public static final ContextParameter<Entity> TARGET = new ContextParameter<>(NeoApoli.id("target"));
	public static final ContextParameter<Entity> ACTOR = new ContextParameter<>(NeoApoli.id("actor"));

	public static final ContextParameter<BlockState> BLOCK_STATE = new ContextParameter<>(NeoApoli.id("block_state"));

}
