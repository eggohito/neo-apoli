package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class PhasingPowerMixin {

	@Mixin(Entity.class)
	public static abstract class EntityLogicHandler {

		@WrapOperation(method = "method_30022", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"))
		private VoxelShape overrideShapeContextIfPresent(BlockState blockState, BlockView blockView, BlockPos blockPos, Operation<VoxelShape> original) {

			Entity entity = (Entity) (Object) this;

			if (PowersComponent.hasInstances(entity, PhasingPower.Instance.class)) {
				return blockState.getCollisionShape(blockView, blockPos, ShapeContext.of(entity));
			}

			else {
				return original.call(blockState, blockView, blockPos);
			}

		}

	}

	@Mixin(AbstractBlock.AbstractBlockState.class)
	public static abstract class PhasingImpl {

		@Shadow
		protected abstract BlockState asBlockState();

		@Unique
		private final ThreadLocal<WeakReference<Context>> neo_apoli$phasingContext = new ThreadLocal<>();

		@Unique
		private Context neo_apoli$getOrCreatePhasingContext(Entity entity, BlockPos blockPos) {

			Context context = Optional.ofNullable(this.neo_apoli$phasingContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> PhasingPower.createContext(entity, new SavedBlockPosition(entity.getWorld(), blockPos, this.asBlockState(), entity.getWorld().getBlockEntity(blockPos))));

			this.neo_apoli$phasingContext.set(new WeakReference<>(context));
			return context;

		}

		@ModifyExpressionValue(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
		private VoxelShape overrideShapeWhenFulfilled(VoxelShape original, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {

			if (shapeContext instanceof EntityShapeContext entityShapeContext && entityShapeContext.getEntity() != null) {

				Context context = this.neo_apoli$getOrCreatePhasingContext(entityShapeContext.getEntity(), blockPos);
				boolean result = PhasingPower.shouldPhaseDown(context, original);

				this.neo_apoli$phasingContext.remove();

				if (result) {
					return VoxelShapes.empty();
				}

				else {
					return original;
				}

			}

			else {
				return original;
			}

		}

		@WrapWithCondition(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityCollisionHandler;)V"))
		private boolean disableEntityCollisionEffects(Block block, BlockState blockState, World world, BlockPos blockPos, Entity entity, EntityCollisionHandler entityCollisionHandler) {

			Context context = this.neo_apoli$getOrCreatePhasingContext(entity, blockPos);
			boolean result = !PowersComponent.hasInstances(entity, PhasingPower.Instance.class, instance -> instance.isActive(context));

			this.neo_apoli$phasingContext.remove();
			return result;

		}

	}

}
