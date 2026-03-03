package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.SimpleCallbackPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackPowerRevokedPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackPowerRevokedPower> MAP_CODEC = createSimpleCallbackCodec(CallbackPowerRevokedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerRevokedPower> STREAM_CODEC = createSimpleCallbackStreamCodec(CallbackPowerRevokedPower::new);

	public CallbackPowerRevokedPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_POWER_REVOKED;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<CallbackPowerRevokedPower> {

		protected Instance(@NotNull CallbackPowerRevokedPower power) {
			super(power);
		}

		@Override
		public void onRevoked(Entity holder) {

			super.onRevoked(holder);
			Context context = createHolderContext(holder);

			if (this.isActive(context)) {
				power.getAction().execute(context.forChild(".action"));
			}

		}

	}

}
