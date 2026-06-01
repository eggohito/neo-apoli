package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.api.power.PowersBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ClearPowersAction(BooleanProvider withCallback, EntityProvider entity) implements Action {

	public static final MapCodec<ClearPowersAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BooleanProvider.CODEC.optionalFieldOf("with_callback", new ConstantBooleanProvider(true)).forGetter(ClearPowersAction::withCallback),
		EntityProvider.CODEC.fieldOf("entity").forGetter(ClearPowersAction::entity)
	).apply(instance, ClearPowersAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ClearPowersAction> STREAM_CODEC = StreamCodec.composite(
		BooleanProvider.STREAM_CODEC, ClearPowersAction::withCallback,
		EntityProvider.STREAM_CODEC, ClearPowersAction::entity,
		ClearPowersAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.CLEAR_POWERS;
	}

	@Override
	public void execute(Context context) {

		PowersBuilder powersBuilder = entity().getEntity(context.forChild(".entity"))
			.filter(Powers::has)
			.map(Powers::builder)
			.orElse(null);

		if (powersBuilder == null) {
			return;
		}

		boolean withCallback = withCallback().getBoolean(context.forChild(".with_callback"));

		for (var holder : powersBuilder.getAll()) {

			for (var source : powersBuilder.getSources(holder.id())) {
				powersBuilder.revoke(holder.id(), source, withCallback);
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		withCallback().validate(validator.forChild(".with_callback"));
		entity().validate(validator.forChild(".entity"));
	}

}
