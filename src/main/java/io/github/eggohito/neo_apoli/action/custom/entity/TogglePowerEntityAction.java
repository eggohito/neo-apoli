package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.power.custom.TogglePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record TogglePowerEntityAction(PowerReference power) implements EntityAction {

	public static final MapCodec<TogglePowerEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(PowerReference.CODEC.fieldOf("power").forGetter(TogglePowerEntityAction::power))
		.apply(instance, TogglePowerEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, TogglePowerEntityAction> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, TogglePowerEntityAction::power,
		TogglePowerEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.TOGGLE_POWER;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity entity = context.getRequired(NeoApoliContextParams.THIS_ENTITY);
		Power.Instance<?> instance = NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.map(powersComponent -> powersComponent.getInstance(this.power()))
			.orElse(null);

		if (instance instanceof TogglePower.Instance toggleInstance) {
			toggleInstance.toggle(entity, context);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		this.power().validate(validator.forChild(".power"));
	}

}
