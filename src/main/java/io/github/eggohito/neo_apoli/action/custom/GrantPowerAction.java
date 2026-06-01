package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.api.power.PowersBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.ParsedArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record GrantPowerAction(ParsedArgument<PowerArgument.Result> power, ResourceLocation source, BooleanProvider withCallback, EntityProvider entity) implements Action {

	public static final MapCodec<GrantPowerAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.POWER_OR_TAG_ARGUMENT.fieldOf("power").forGetter(GrantPowerAction::power),
		ResourceLocation.CODEC.fieldOf("source").forGetter(GrantPowerAction::source),
		BooleanProvider.CODEC.optionalFieldOf("with_callback", new ConstantBooleanProvider(true)).forGetter(GrantPowerAction::withCallback),
		EntityProvider.CODEC.fieldOf("entity").forGetter(GrantPowerAction::entity)
	).apply(instance, GrantPowerAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GrantPowerAction> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.POWER_OR_TAG_ARGUMENT, GrantPowerAction::power,
		ResourceLocation.STREAM_CODEC, GrantPowerAction::source,
		BooleanProvider.STREAM_CODEC, GrantPowerAction::withCallback,
		EntityProvider.STREAM_CODEC, GrantPowerAction::entity,
		GrantPowerAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.GRANT_POWER;
	}

	@Override
	public void execute(Context context) {

		PowersBuilder powersBuilder = entity().getEntity(context.forChild(".entity"))
			.map(Powers::builder)
			.orElse(null);

		if (powersBuilder == null) {
			return;
		}

		try {

			boolean withCallback = withCallback().getBoolean(context.forChild(".with_callback"));

			for (var holder : power().argument().get()) {
				powersBuilder.grant(holder.id(), source(), withCallback);
			}

			powersBuilder.build();

		}

		catch (CommandSyntaxException e) {
			context.reportProblem(e.getMessage());
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		power().argument().validate(validator.forChild(".power"));
		withCallback().validate(validator.forChild(".with_callback"));
		entity().validate(validator.forChild(".entity"));
	}

}
