package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConsumeItemAction(NumberProvider amount) implements ItemAction {

	public static final MapCodec<ConsumeItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("amount").forGetter(ConsumeItemAction::amount)
	).apply(instance, ConsumeItemAction::new));

	public static final PacketCodec<RegistryByteBuf, ConsumeItemAction> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, ConsumeItemAction::amount,
		ConsumeItemAction::new
	);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.CONSUME;
	}

	@Override
	public void execute(Context context) {

		if (context.getWorld().isClient()) {
			return;
		}

		Context amountContext = context.makeChild("amount");
		int amount = this.amount().intValue(amountContext);

		if (!amountContext.hasErrors()) {
			context.required(ContextParameters.ITEM_STACK).decrement(amount);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		ItemAction.super.validate(reporter);
		amount().validate(reporter.makeChild("amount"));
	}

}
