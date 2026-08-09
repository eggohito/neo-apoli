package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.entity.MutablePowers;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ClearPowersAction(EntityProvider entity) implements Action {

	public static final MapCodec<ClearPowersAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityProvider.CODEC.fieldOf("entity").forGetter(ClearPowersAction::entity)
	).apply(instance, ClearPowersAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ClearPowersAction> STREAM_CODEC = StreamCodec.composite(
		EntityProvider.STREAM_CODEC, ClearPowersAction::entity,
		ClearPowersAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.CLEAR_POWERS;
	}

	@Override
	public void execute(Context context) {

		MutablePowers mutablePowers = entity().getEntity(context.forChild(".entity"))
			.flatMap(MutablePowers::getOptional)
			.orElse(null);

		if (mutablePowers == null) {
			return;
		}

		for (var holder : mutablePowers.getAll()) {

			for (var source : mutablePowers.getSources(holder.id())) {
				mutablePowers.revoke(holder.id(), source);
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		entity().validate(validator.forChild(".entity"));
	}

}
