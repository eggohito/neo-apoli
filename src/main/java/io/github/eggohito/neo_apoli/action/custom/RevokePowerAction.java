package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.power.entity.PowersBuilder;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.ParsedArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RevokePowerAction(ParsedArgument<PowerArgument.Result> power, ResourceLocation source, BooleanProvider withCallback, EntityProvider entity) implements Action {

	public static final MapCodec<RevokePowerAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.POWER_OR_TAG_ARGUMENT.fieldOf("power").forGetter(RevokePowerAction::power),
		ResourceLocation.CODEC.fieldOf("source").forGetter(RevokePowerAction::source),
		BooleanProvider.CODEC.optionalFieldOf("with_callback", new ConstantBooleanProvider(true)).forGetter(RevokePowerAction::withCallback),
		EntityProvider.CODEC.fieldOf("entity").forGetter(RevokePowerAction::entity)
	).apply(instance, RevokePowerAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RevokePowerAction> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.POWER_OR_TAG_ARGUMENT, RevokePowerAction::power,
		ResourceLocation.STREAM_CODEC, RevokePowerAction::source,
		BooleanProvider.STREAM_CODEC,  RevokePowerAction::withCallback,
		EntityProvider.STREAM_CODEC, RevokePowerAction::entity,
		RevokePowerAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.REVOKE_POWER;
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

		try {

			boolean withCallback = withCallback().getBoolean(context.forChild(".with_callback"));

			for (var holder : power.argument().get()) {
				powersBuilder.revoke(holder.id(), source(), withCallback);
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
