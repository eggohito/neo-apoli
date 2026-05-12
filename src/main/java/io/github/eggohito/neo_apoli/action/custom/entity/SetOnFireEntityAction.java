package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record SetOnFireEntityAction(NumberProvider ticks) implements EntityAction {

	public static final MapCodec<SetOnFireEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NumberProvider.CODEC.fieldOf("ticks").forGetter(SetOnFireEntityAction::ticks))
		.apply(instance, SetOnFireEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetOnFireEntityAction> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, SetOnFireEntityAction::ticks,
		SetOnFireEntityAction::new
	);

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.SET_ON_FIRE;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity entity = context.getRequired(NeoApoliContextParams.THIS_ENTITY);
		int ticks = ticks().nextInt(context.forChild(".ticks"));

		if (ticks > 0) {
			entity.igniteForTicks(ticks);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		ticks().validate(validator.forChild(".ticks"));
	}

}
