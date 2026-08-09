package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.power.entity.PowersBuilder;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RevokeAllPowersAction(ResourceLocation source, BooleanProvider withCallback, EntityProvider entity) implements Action {

	public static final MapCodec<RevokeAllPowersAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("source").forGetter(RevokeAllPowersAction::source),
		BooleanProvider.CODEC.optionalFieldOf("with_callback", new ConstantBooleanProvider(true)).forGetter(RevokeAllPowersAction::withCallback),
		EntityProvider.CODEC.fieldOf("entity").forGetter(RevokeAllPowersAction::entity)
	).apply(instance, RevokeAllPowersAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RevokeAllPowersAction> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, RevokeAllPowersAction::source,
		BooleanProvider.STREAM_CODEC, RevokeAllPowersAction::withCallback,
		EntityProvider.STREAM_CODEC, RevokeAllPowersAction::entity,
		RevokeAllPowersAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.REVOKE_ALL_POWERS;
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

		for (var holder : powersBuilder.getAllFromSource(source())) {
			powersBuilder.revoke(holder.id(), source(), withCallback);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		withCallback().validate(validator.forChild(".with_callback"));
		entity().validate(validator.forChild(".entity"));
	}

}
