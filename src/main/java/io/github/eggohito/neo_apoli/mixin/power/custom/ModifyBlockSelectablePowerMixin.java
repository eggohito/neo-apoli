package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.custom.ModifyBlockSelectablePower;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class ModifyBlockSelectablePowerMixin extends StateHolder<Block, BlockState> {

	@Shadow
	protected abstract BlockState asState();

	protected ModifyBlockSelectablePowerMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec) {
		super(owner, propertyMap, codec);
	}

	@ModifyReturnValue(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
	private VoxelShape neo_apoli$modifySelectableShape(VoxelShape original, BlockGetter blockView, BlockPos blockPos, CollisionContext shapeContext) {

		if (shapeContext instanceof EntityCollisionContext entityShapeContext && entityShapeContext.getEntity() != null) {

			Context context = ModifyBlockSelectablePower.createContext(entityShapeContext.getEntity(), blockPos, this.asState(), blockView.getBlockEntity(blockPos));

			return ModifyBlockSelectablePower.modify(context, () -> original);

		}

		else {
			return original;
		}

	}

}
