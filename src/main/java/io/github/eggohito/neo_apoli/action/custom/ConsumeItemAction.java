package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.slot.SlotProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConsumeItemAction(NumberProvider amount, SlotProvider slot) implements Action {

	public static final MapCodec<ConsumeItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("amount", new ConstantNumberProvider(1)).forGetter(ConsumeItemAction::amount),
		SlotProvider.CODEC.fieldOf("slot").forGetter(ConsumeItemAction::slot)
	).apply(instance, ConsumeItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeItemAction> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, ConsumeItemAction::amount,
		SlotProvider.STREAM_CODEC, ConsumeItemAction::slot,
		ConsumeItemAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.CONSUME_ITEM;
	}

	@Override
	public void execute(Context context) {

		if (context.level().isClientSide()) {
			return;
		}

		int amount = Math.abs(amount().getInt(context.forChild(".amount")));
		slot().getSlot(context.forChild(".slot")).get().shrink(amount);

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
		slot().validate(validator.forChild(".slot"));
	}

}
