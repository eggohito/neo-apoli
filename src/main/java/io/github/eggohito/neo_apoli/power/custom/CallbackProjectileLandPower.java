package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.SimpleCallbackPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.AABBUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
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

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackProjectileLandPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackProjectileLandPower> CODEC = SimpleCallbackPower.createSimpleCallbackCodec(CallbackProjectileLandPower::new);
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

		public void execute(Context context) {
			power.getAction().execute(context.makeChild(".action"));
		}

	}

	public static void execute(Context context, List<Instance> instances) {

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {
					instance.execute(instanceContext);
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

	}

	public static Context createContext(Entity owner, Projectile projectile, HitResult hitResult) {

		Level level = projectile.level();
		Vec3 pos = hitResult.getLocation();

		Entity target;
		BlockPos blockPos;
		Direction side;

		switch (hitResult) {
			case EntityHitResult entityHitResult -> {
				target = entityHitResult.getEntity();
				blockPos = BlockPos.containing(pos);
				side = AABBUtil.getSideFromPoint(target.getBoundingBox(), pos);
			}
			case BlockHitResult blockHitResult -> {
				target = null;
				blockPos = blockHitResult.getBlockPos();
				side = blockHitResult.getDirection();
			}
			default -> {
				target = null;
				blockPos = BlockPos.containing(pos);
				side = null;
			}
		}

		return PowerTypes.CALLBACK_PROJECTILE_LAND.contextBuilder()
			.add(NeoApoliContextKeys.ACTOR_ENTITY, owner)
			.addNullable(NeoApoliContextKeys.TARGET_ENTITY, target)
			.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, level.getBlockState(blockPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, level.getBlockEntity(blockPos))
			.addNullable(NeoApoliContextKeys.DIRECTION, side)
			.add(NeoApoliContextKeys.PROJECTILE_ENTITY, projectile)
			.add(NeoApoliContextKeys.THIS_ENTITY, owner)
			.add(NeoApoliContextKeys.THIS_POS, owner.position())
			.build(level);

	}

}
