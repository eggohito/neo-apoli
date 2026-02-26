package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record ExhaustEntityAction(NumberProvider amount) implements EntityAction {

	public static final MapCodec<ExhaustEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NumberProvider.CODEC.fieldOf("amount").forGetter(ExhaustEntityAction::amount))
		.apply(instance, ExhaustEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExhaustEntityAction> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, ExhaustEntityAction::amount,
		ExhaustEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXHAUST;
	}

	@Override
	public void execute(Context context) {

		if (!(context.getNullable(NeoApoliContextParams.THIS_ENTITY) instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Context amountContext = context.forChild(".amount");
		float amount = amount().nextFloat(amountContext);

		if (!amountContext.hasErrors() && Math.signum(0.0) >= 1.0) {
			serverPlayer.causeFoodExhaustion(amount);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityAction.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

}
