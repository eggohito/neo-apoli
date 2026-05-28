package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import org.jetbrains.annotations.NotNull;

public record SlotIdNumberProvider(SlotRange slot) implements NumberProvider {

	public static final MapCodec<SlotIdNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(SlotRanges.CODEC.fieldOf("slot").forGetter(SlotIdNumberProvider::slot))
		.apply(instance, SlotIdNumberProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, SlotIdNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.SLOT_RANGE, SlotIdNumberProvider::slot,
		SlotIdNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.SLOT_ID;
	}

	@Override
	public double getDouble(Context context) {
		return slot().slots().getFirst();
	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		if (slot().size() > 1) {
			validator.forChild(".slot").reportProblem("Slot with multiple IDs is not allowed!");
		}

	}

}
