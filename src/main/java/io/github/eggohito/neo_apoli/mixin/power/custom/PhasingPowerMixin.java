package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

public abstract class PhasingPowerMixin {

	@Mixin(Entity.class)
	public static abstract class EntityLogicHandler {

		@WrapOperation(method = "method_30022", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
		private VoxelShape overrideShapeContextIfPresent(BlockState blockState, BlockGetter blockView, BlockPos blockPos, Operation<VoxelShape> original) {

			Entity entity = (Entity) (Object) this;

			if (PowersComponent.hasInstances(entity, PhasingPower.Instance.class)) {
				return blockState.getCollisionShape(blockView, blockPos, CollisionContext.of(entity));
			}

			else {
				return original.call(blockState, blockView, blockPos);
			}

		}

	}

	@Mixin(BlockBehaviour.BlockStateBase.class)
	public static abstract class PhasingImpl {

		@Shadow
		protected abstract BlockState asState();

		@ModifyExpressionValue(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
		private VoxelShape overrideShapeWhenFulfilled(VoxelShape original, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collision) {

			if (MiscUtil.collisionHasEntity(collision)) {

				Entity entity = Objects.requireNonNull(MiscUtil.getEntityFromCollision(collision));
				CachedBlock cachedBlock = new CachedBlock(entity.level(), blockPos, this.asState(), blockGetter.getBlockEntity(blockPos));

				if (PhasingPower.doesApply(entity, cachedBlock, original)) {
					return Shapes.empty();
				}

			}

			return original;

		}

		@WrapWithCondition(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;entityInside(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/InsideBlockEffectApplier;)V"))
		private boolean disableEntityCollisionEffects(Block block, BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
			return !PhasingPower.doesApply(entity, new CachedBlock(level, blockPos, blockState, level.getBlockEntity(blockPos)), Power.Instance::isActive);
		}

	}

}
