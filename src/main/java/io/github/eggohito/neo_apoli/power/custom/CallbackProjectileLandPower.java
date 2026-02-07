package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.SimpleCallbackPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.AABBUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackProjectileLandPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackProjectileLandPower> MAP_CODEC = SimpleCallbackPower.createSimpleCallbackCodec(CallbackProjectileLandPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackProjectileLandPower> STREAM_CODEC = SimpleCallbackPower.createSimpleCallbackStreamCodec(CallbackProjectileLandPower::new);

	public CallbackProjectileLandPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_PROJECTILE_LAND;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CallbackProjectileLandPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackProjectileLandPower power) {
			super(holder, power);
		}

		public Context createContext(Entity owner, Projectile projectile, HitResult result) {

			Level level = projectile.level();
			Vec3 pos = result.getLocation();

			Entity target;
			BlockPos blockPos;
			Direction side;

			switch (result) {
				case EntityHitResult entityResult -> {
					target = entityResult.getEntity();
					blockPos = BlockPos.containing(pos);
					side = AABBUtil.getSideFromPoint(target.getBoundingBox(), pos);
				}
				case BlockHitResult blockResult -> {
					target = null;
					blockPos = blockResult.getBlockPos();
					side = blockResult.getDirection();
				}
				default -> {
					target = null;
					blockPos = BlockPos.containing(pos);
					side = null;
				}
			}

			return this.createHolderContextBuilder()
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, owner)
				.withNullable(NeoApoliContextParams.TARGET_ENTITY, target)
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(blockPos))
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(blockPos))
				.withNullable(NeoApoliContextParams.DIRECTION, side)
				.withRequired(NeoApoliContextParams.PROJECTILE_ENTITY, projectile)
				.buildWithRequirements(holder.level(), PowerTypes.CALLBACK_PROJECTILE_LAND.keySet());

		}

		public void execute(Context context) {
			power.getAction().execute(context.forChild(".action"));
		}

	}

	public static void executeAsOwner(Entity owner, Projectile projectile, HitResult result) {
		execute(owner, owner, projectile, result);
	}

	public static void executeAsProjectile(Entity owner, Projectile projectile, HitResult result) {
		execute(owner, projectile, projectile, result);
	}

	public static void execute(Entity owner, Entity powerHolder, Projectile projectile, HitResult result) {

		for (var instance : PowersComponent.getInstances(powerHolder, Instance.class)) {

			Context context = instance.createContext(owner, projectile, result);

			if (instance.isActive(context)) {
				instance.execute(context);
			}

		}

	}

}
