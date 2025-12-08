package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.SimpleCallbackPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class CallbackPlayerRespawnedPower extends SimpleCallbackPower {

	public static final MapCodec<CallbackPlayerRespawnedPower> CODEC = createSimpleCallbackCodec(CallbackPlayerRespawnedPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CallbackPlayerRespawnedPower> STREAM_CODEC = createSimpleCallbackStreamCodec(CallbackPlayerRespawnedPower::new);

	public CallbackPlayerRespawnedPower(Optional<Condition> activeCondition, Action action) {
		super(activeCondition, action);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CALLBACK_PLAYER_RESPAWNED;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CallbackPlayerRespawnedPower> {

		protected Instance(@NotNull Entity holder, @NotNull CallbackPlayerRespawnedPower power) {
			super(holder, power);
		}

		@Override
		public void onRespawned() {

			super.onRespawned();
			Context context = createHolderContext();

			if (this.isActive(context)) {
				power.getAction().execute(context.makeChild(".action"));
			}

		}

	}

}
