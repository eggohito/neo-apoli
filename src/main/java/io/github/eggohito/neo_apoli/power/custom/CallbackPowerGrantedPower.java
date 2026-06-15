package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.misc.CallbackPower;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record CallbackPowerGrantedPower(Optional<Condition> activeCondition, Action action) implements CallbackPower {

	public static final MapCodec<CallbackPowerGrantedPower> CODEC = CallbackPower.codec(CallbackPowerGrantedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPowerGrantedPower> STREAM_CODEC = CallbackPower.streamCodec(CallbackPowerGrantedPower::new);

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.CALLBACK_POWER_GRANTED;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<CallbackPowerGrantedPower> {

		protected Instance(@NotNull CallbackPowerGrantedPower power) {
			super(power);
		}

		@Override
		public void onGranted(Entity holder) {

			super.onGranted(holder);
			Context context = createHolderContext(holder);

			if (this.isActive(context)) {
				power.action().execute(context.forChild(".action"));
			}

		}

	}

}
