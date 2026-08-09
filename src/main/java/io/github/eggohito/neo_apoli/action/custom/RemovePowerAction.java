package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.command.argument.PowerArgument;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.entity.MutablePowers;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.ParsedArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RemovePowerAction(ParsedArgument<PowerArgument.Result> power, BooleanProvider withCallback, EntityProvider entity) implements Action {

	public static final MapCodec<RemovePowerAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.POWER_OR_TAG_ARGUMENT.fieldOf("power").forGetter(RemovePowerAction::power),
		BooleanProvider.CODEC.optionalFieldOf("with_callback", new ConstantBooleanProvider(true)).forGetter(RemovePowerAction::withCallback),
		EntityProvider.CODEC.fieldOf("entity").forGetter(RemovePowerAction::entity)
	).apply(instance, RemovePowerAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RemovePowerAction> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.POWER_OR_TAG_ARGUMENT, RemovePowerAction::power,
		BooleanProvider.STREAM_CODEC, RemovePowerAction::withCallback,
		EntityProvider.STREAM_CODEC, RemovePowerAction::entity,
		RemovePowerAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.REMOVE_POWER;
	}

	@Override
	public void execute(Context context) {

		MutablePowers mutablePowers = entity().getEntity(context.forChild(".entity"))
			.filter(Powers::has)
			.map(MutablePowers::create)
			.orElse(null);

		if (mutablePowers == null) {
			return;
		}

		try {

			boolean withCallback = withCallback().getBoolean(context.forChild(".with_callback"));

			for (var holder : power().argument().get()) {

				for (var source : mutablePowers.getSources(holder.id())) {
					mutablePowers.revoke(holder.id(), source, withCallback);
				}

			}

			mutablePowers.applyChanges();

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
