package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.SimpleCallbackPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackPlayerWakeUpPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackPlayerWakeUpPower> CODEC = SimpleCallbackPower.createSimpleCallbackCodec(CallbackPlayerWakeUpPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPlayerWakeUpPower> STREAM_CODEC = SimpleCallbackPower.createSimpleCallbackStreamCodec(CallbackPlayerWakeUpPower::new);

	public CallbackPlayerWakeUpPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_PLAYER_WAKE_UP;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CallbackPlayerWakeUpPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackPlayerWakeUpPower power) {
			super(holder, power);
		}

		public void execute(Context context) {
			power.getAction().execute(context.makeChild(".action"));
		}

	}

	public static void execute(Player player, BlockPos sleepingPos) {

		List<Instance> instances = PowersComponent.getInstances(player, Instance.class);
		Context context = createContext(player, sleepingPos);

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

	public static Context createContext(Player player, BlockPos sleepingPos) {
		Level level = player.level();
		return PowerTypes.CALLBACK_PLAYER_WAKE_UP.contextBuilder()
			.add(NeoApoliContextKeys.BLOCK_POS, sleepingPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, level.getBlockState(sleepingPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, level.getBlockEntity(sleepingPos))
			.add(NeoApoliContextKeys.THIS_ENTITY, player)
			.add(NeoApoliContextKeys.THIS_POS, player.position())
			.build(level);
	}

}
