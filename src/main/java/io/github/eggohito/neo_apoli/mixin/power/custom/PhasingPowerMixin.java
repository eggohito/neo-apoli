package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

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

		@Unique
		private final ThreadLocal<WeakReference<Context>> neo_apoli$phasingContext = new ThreadLocal<>();

		@Unique
		private Context neo_apoli$getOrCreatePhasingContext(Entity entity, BlockPos blockPos) {

			Context context = Optional.ofNullable(this.neo_apoli$phasingContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> PhasingPower.createContext(entity, new SavedBlockPosition(entity.level(), blockPos, this.asState(), entity.level().getBlockEntity(blockPos))));

			this.neo_apoli$phasingContext.set(new WeakReference<>(context));
			return context;

		}

		@ModifyExpressionValue(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
		private VoxelShape overrideShapeWhenFulfilled(VoxelShape original, BlockGetter blockView, BlockPos blockPos, CollisionContext shapeContext) {

			if (shapeContext instanceof EntityCollisionContext entityShapeContext && entityShapeContext.getEntity() != null) {

				Context context = this.neo_apoli$getOrCreatePhasingContext(entityShapeContext.getEntity(), blockPos);
				boolean result = PhasingPower.shouldPhase(context, original);

				this.neo_apoli$phasingContext.remove();

				if (result) {
					return Shapes.empty();
				}

				else {
					return original;
				}

			}

			else {
				return original;
			}

		}

		@WrapWithCondition(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;entityInside(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/InsideBlockEffectApplier;)V"))
		private boolean disableEntityCollisionEffects(Block block, BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {

			Context context = this.neo_apoli$getOrCreatePhasingContext(entity, blockPos);
			boolean result = !PhasingPower.shouldPhase(context, Power.Instance::isActive);

			this.neo_apoli$phasingContext.remove();
			return result;

		}

	}

}
