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
public class CallbackPowerAddedPower extends CallbackPower {

	public static final MapCodec<CallbackPowerAddedPower> CODEC = createSimpleCallbackCodec(CallbackPowerAddedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerAddedPower> STREAM_CODEC = createSimpleCallbackStreamCodec(CallbackPowerAddedPower::new);

	public CallbackPowerAddedPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.CALLBACK_POWER_ADDED;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<CallbackPowerAddedPower> {

		protected Instance(@NotNull CallbackPowerAddedPower power) {
			super(power);
		}

		@Override
		public void onAdded(Entity holder) {

			super.onAdded(holder);
			Context context = createHolderContext(holder);

			if (this.isActive(context)) {
				power.getAction().execute(context.forChild(".action"));
			}

		}

	}

}
