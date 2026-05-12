package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliItemActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.SlotAccess;

public record ConsumeItemAction(NumberProvider amount) implements ItemAction {

	public static final MapCodec<ConsumeItemAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("amount").forGetter(ConsumeItemAction::amount)
	).apply(instance, ConsumeItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeItemAction> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, ConsumeItemAction::amount,
		ConsumeItemAction::new
	);

	@Override
	public ItemAction.Type<?> getType() {
		return NeoApoliItemActionTypes.CONSUME;
	}

	@Override
	public void execute(Context context) {

		if (context.level().isClientSide() || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		SlotAccess slotAccess = context.getRequired(NeoApoliContextParams.SLOT_ACCESS);
		int amount = Math.abs(amount().nextInt(context.forChild(".amount")));

		slotAccess.get().shrink(amount);

	}

	@Override
	public void validate(Context.Validator validator) {
		ItemAction.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

}
