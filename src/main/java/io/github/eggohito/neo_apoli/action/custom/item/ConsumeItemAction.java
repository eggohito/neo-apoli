package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
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
	public void serverExecute(ServerContext context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		ServerContext amountContext = context.makeChild(".amount");
		int amount = Math.abs(amount().nextInt(amountContext));

		if (!amountContext.hasErrors()) {
			context.required(ContextParameters.STACK_REFERENCE).get().decrement(amount);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		ItemAction.super.validate(reporter);
		amount().validate(reporter.makeChild(".amount"));
	}

}
