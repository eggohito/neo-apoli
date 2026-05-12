package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.misc.CallbackPower;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackPlayerWakeUpPower extends CallbackPower {

	public static final MapCodec<CallbackPlayerWakeUpPower> MAP_CODEC = CallbackPower.createSimpleCallbackCodec(CallbackPlayerWakeUpPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPlayerWakeUpPower> STREAM_CODEC = CallbackPower.createSimpleCallbackStreamCodec(CallbackPlayerWakeUpPower::new);

	public CallbackPlayerWakeUpPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.CALLBACK_PLAYER_WAKE_UP;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<CallbackPlayerWakeUpPower> {

		protected Instance(@NotNull CallbackPlayerWakeUpPower power) {
			super(power);
		}

		public Context createContext(Entity holder, BlockPos sleepingPos) {
			Level level = holder.level();
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.BLOCK_POS, sleepingPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(sleepingPos))
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(sleepingPos))
				.buildWithRequirements(level, NeoApoliPowerTypes.CALLBACK_PLAYER_WAKE_UP.keySet());
		}

		public void execute(Context context) {
			power.getAction().execute(context.forChild(".action"));
		}

	}

	public static void execute(Player sleeper, BlockPos sleepingPos) {

		for (var instance : Powers.getInstances(sleeper, Instance.class)) {

			Context context = instance.createContext(sleeper, sleepingPos);

			if (instance.isActive(context)) {
				instance.execute(context);
			}

		}

	}

}
