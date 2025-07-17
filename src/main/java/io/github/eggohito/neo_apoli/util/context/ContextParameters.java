package io.github.eggohito.neo_apoli.util.context;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class ContextParameters {

	public static final ContextParameter<PowerReference> POWER_REFERENCE = new ContextParameter<>(NeoApoli.id("power_reference"));
	public static final ContextParameter<Vec3d> POSITION = new ContextParameter<>(NeoApoli.id("position"));

	public static final ContextParameter<Entity> ACTOR = new ContextParameter<>(NeoApoli.id("actor"));
	public static final ContextParameter<Entity> TARGET = new ContextParameter<>(NeoApoli.id("target"));

	public static final ContextParameter<BlockState> BLOCK_STATE = new ContextParameter<>(NeoApoli.id("block_state"));
	public static final ContextParameter<BlockEntity> BLOCK_ENTITY = new ContextParameter<>(NeoApoli.id("block_entity"));
	public static final ContextParameter<Direction> DIRECTION = new ContextParameter<>(NeoApoli.id("direction"));

	public static final ContextParameter<Entity> THIS_ENTITY = new ContextParameter<>(NeoApoli.id("this_entity"));

	public static final ContextParameter<StackReference> STACK_REFERENCE = new ContextParameter<>(NeoApoli.id("stack_reference"));
	public static final ContextParameter<ItemStack> ITEM_STACK = new ContextParameter<>(NeoApoli.id("item_stack"));
	public static final ContextParameter<Hand> HAND = new ContextParameter<>(NeoApoli.id("hand"));

}
