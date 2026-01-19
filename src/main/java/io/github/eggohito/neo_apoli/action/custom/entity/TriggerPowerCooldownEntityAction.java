package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.CooldownPower;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record TriggerPowerCooldownEntityAction(PowerReference power) implements EntityAction {

	public static final MapCodec<TriggerPowerCooldownEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(PowerReference.CODEC.fieldOf("power").forGetter(TriggerPowerCooldownEntityAction::power))
		.apply(instance, TriggerPowerCooldownEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, TriggerPowerCooldownEntityAction> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, TriggerPowerCooldownEntityAction::power,
		TriggerPowerCooldownEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.TRIGGER_POWER_COOLDOWN;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity entity = context.required(NeoApoliContextKeys.THIS_ENTITY);
		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);

		if (powersComponent.getNullableInstance(this.power()) instanceof CooldownPower.Instance cooldownInstance) {
			cooldownInstance.trigger();
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		this.power().validate(validator.forChild(".power"));
	}

}
