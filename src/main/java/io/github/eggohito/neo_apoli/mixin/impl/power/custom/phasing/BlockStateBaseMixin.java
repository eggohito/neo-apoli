package io.github.eggohito.neo_apoli.mixin.impl.power.custom.phasing;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

	@Shadow
	protected abstract BlockState asState();

	@ModifyExpressionValue(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
	VoxelShape emptyCollisionShapeIfAffectedByPhasing(VoxelShape original, BlockGetter level, BlockPos blockPos, CollisionContext collision) {

		if (MiscUtil.collisionHasEntity(collision)) {

			Entity entity = Objects.requireNonNull(MiscUtil.getEntityFromCollision(collision));
			CachedBlock cachedBlock = new CachedBlock(blockPos, this.asState(), level.getBlockEntity(blockPos));

			if (PhasingPower.doesApply(entity, cachedBlock, original)) {
				return Shapes.empty();
			}

		}

		return original;

	}

	@ModifyExpressionValue(method = "getEntityInsideCollisionShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getEntityInsideCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
	VoxelShape emptyEntityInsideShapeIfAffectedByPhasing(VoxelShape original, BlockGetter level, BlockPos pos, Entity entity) {

		if (PhasingPower.doesApply(entity, new CachedBlock(pos, this.asState(), level.getBlockEntity(pos)), original)) {
			return Shapes.empty();
		}

		else {
			return original;
		}

	}

}
