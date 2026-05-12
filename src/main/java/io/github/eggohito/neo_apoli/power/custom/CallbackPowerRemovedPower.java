package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.misc.CallbackPower;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackPowerRemovedPower extends CallbackPower {

	public static final MapCodec<CallbackPowerRemovedPower> MAP_CODEC = createSimpleCallbackCodec(CallbackPowerRemovedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerRemovedPower> STREAM_CODEC = createSimpleCallbackStreamCodec(CallbackPowerRemovedPower::new);

	public CallbackPowerRemovedPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.CALLBACK_POWER_REMOVED;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<CallbackPowerRemovedPower> {

		protected Instance(@NotNull CallbackPowerRemovedPower power) {
			super(power);
		}

		@Override
		public void onRemoved(Entity holder) {

			super.onRemoved(holder);
			Context context = createHolderContext(holder);

			if (this.isActive(context)) {
				power.getAction().execute(context.forChild(".action"));
			}

		}

	}

}
