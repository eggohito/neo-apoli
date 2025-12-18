package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConsumeItemAction(NumberProvider amount) implements ItemAction {

	public static final MapCodec<ConsumeItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("amount").forGetter(ConsumeItemAction::amount)
	).apply(instance, ConsumeItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeItemAction> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, ConsumeItemAction::amount,
		ConsumeItemAction::new
	);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.CONSUME;
	}

	@Override
	public void execute(Context context) {

		if (!context.getLevel().isClientSide() || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Context amountContext = context.forChild(".amount");
		int amount = Math.abs(amount().nextInt(amountContext));

		if (!amountContext.hasErrors()) {
			context.required(NeoApoliContextKeys.STACK_REFERENCE).get().shrink(amount);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		ItemAction.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

}
