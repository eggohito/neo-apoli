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
public class CallbackPlayerWakeUpPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackPlayerWakeUpPower> MAP_CODEC = SimpleCallbackPower.createSimpleCallbackCodec(CallbackPlayerWakeUpPower::new);
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

		public Context createContext(BlockPos sleepingPos) {
			Level level = holder.level();
			return this.createHolderContextBuilder()
				.withRequired(NeoApoliContextParams.BLOCK_POS, sleepingPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(sleepingPos))
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(sleepingPos))
				.buildWithRequirements(level, PowerTypes.CALLBACK_PLAYER_WAKE_UP.keySet());
		}

		public void execute(Context context) {
			power.getAction().execute(context.forChild(".action"));
		}

	}

	public static void execute(Player player, BlockPos sleepingPos) {

		for (var instance : PowersComponent.getInstances(player, Instance.class)) {

			Context context = instance.createContext(sleepingPos);

			if (instance.isActive(context)) {
				instance.execute(context);
			}

		}

	}

}
