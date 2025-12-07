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
public class CallbackPowerRevokedPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackPowerRevokedPower> CODEC = createSimpleCallbackCodec(CallbackPowerRevokedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerRevokedPower> STREAM_CODEC = createSimpleCallbackStreamCodec(CallbackPowerRevokedPower::new);

	public CallbackPowerRevokedPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_POWER_REVOKED;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new io.github.eggohito.neo_apoli.power.custom.CallbackPowerRevokedPower.Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CallbackPowerRevokedPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackPowerRevokedPower power) {
			super(holder, power);
		}

		@Override
		public void onRevoked() {

			super.onRevoked();
			Context context = createHolderContext();

			if (this.isActive(context)) {
				power.getAction().execute(context.makeChild(".action"));
			}

		}

	}

}
