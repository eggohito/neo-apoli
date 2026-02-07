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
public class CallbackPowerGrantedPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackPowerGrantedPower> MAP_CODEC = createSimpleCallbackCodec(CallbackPowerGrantedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerGrantedPower> STREAM_CODEC = createSimpleCallbackStreamCodec(CallbackPowerGrantedPower::new);

	public CallbackPowerGrantedPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_POWER_GRANTED;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CallbackPowerGrantedPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackPowerGrantedPower power) {
			super(holder, power);
		}

		@Override
		public void onGranted() {

			super.onGranted();
			Context context = createHolderContext();

			if (this.isActive(context)) {
				power.getAction().execute(context.forChild(".action"));
			}

		}

	}

}
