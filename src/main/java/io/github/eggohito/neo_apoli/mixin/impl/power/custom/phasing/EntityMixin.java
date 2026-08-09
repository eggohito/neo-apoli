package io.github.eggohito.neo_apoli.mixin.impl.power.custom.phasing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

	/**
	 * Overrides the {@link BlockState#getCollisionShape(BlockGetter, BlockPos)} call in {@link Entity#isInWall()}
	 * to include the entity in the collision context if it has a power that uses the {@link PhasingPower phasing} power type.
	 */
	@WrapOperation(method = "method_30022", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
	VoxelShape includeEntityInCollision(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Operation<VoxelShape> original) {
		Entity entity = (Entity) (Object) this;
		return Powers.hasInstances(entity, PhasingPower.Instance.class)
			? blockState.getCollisionShape(blockGetter, blockPos, CollisionContext.of(entity))
			: original.call(blockState, blockGetter, blockPos);
	}

}
