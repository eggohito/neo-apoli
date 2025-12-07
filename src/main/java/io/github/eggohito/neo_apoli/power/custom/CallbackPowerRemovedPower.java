package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.SimpleCallbackPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class CallbackPowerRemovedPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackPowerRemovedPower> CODEC = createSimpleCallbackCodec(CallbackPowerRemovedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerRemovedPower> STREAM_CODEC = createSimpleCallbackStreamCodec(CallbackPowerRemovedPower::new);

	public CallbackPowerRemovedPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_POWER_REMOVED;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new io.github.eggohito.neo_apoli.power.custom.CallbackPowerRemovedPower.Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CallbackPowerRemovedPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackPowerRemovedPower power) {
			super(holder, power);
		}

		@Override
		public void onRemoved() {

			super.onRemoved();
			Context context = createHolderContext();

			if (this.isActive(context)) {
				power.getAction().execute(context.makeChild(".action"));
			}

		}

	}

}
