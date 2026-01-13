package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.TogglePower;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ToggleEntityAction(PowerReference power) implements EntityAction {

	public static final MapCodec<ToggleEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(PowerReference.CODEC.fieldOf("power").forGetter(ToggleEntityAction::power))
		.apply(instance, ToggleEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleEntityAction> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, ToggleEntityAction::power,
		ToggleEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.TOGGLE;
	}

	@Override
	public void execute(Context context) {

		Power.Instance<?> instance = context.optional(NeoApoliContextKeys.THIS_ENTITY)
			.flatMap(NeoApoliEntityComponents.POWERS::maybeGet)
			.map(powersComponent -> powersComponent.getInstance(this.power()))
			.orElse(null);

		if (instance instanceof TogglePower.Instance toggleInstance) {
			toggleInstance.toggle(context);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		this.power().validate(validator.forChild(".power"));
	}

}
