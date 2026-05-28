package io.github.eggohito.neo_apoli.provider.custom.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.block.BlockProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliSlotProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;

import java.util.Optional;

public record BlockSlotProvider(BlockProvider block, NumberProvider slot) implements SlotProvider {

	public static final MapCodec<BlockSlotProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockProvider.CODEC.fieldOf("block").forGetter(BlockSlotProvider::block),
		NumberProvider.CODEC.fieldOf("slot").forGetter(BlockSlotProvider::slot)
	).apply(instance, BlockSlotProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockSlotProvider> STREAM_CODEC = StreamCodec.composite(
		BlockProvider.STREAM_CODEC, BlockSlotProvider::block,
		NumberProvider.STREAM_CODEC, BlockSlotProvider::slot,
		BlockSlotProvider::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliSlotProviderTypes.BLOCK;
	}

	@Override
	public SlotAccess getSlot(Context context) {

		Context slotContext = context.forChild(".slot");
		int slot = slot().getInt(slotContext);

		if (slotContext.hasErrors()) {
			return SlotAccess.NULL;
		}

		else {

			Optional<CachedBlock> block = block().getBlock(context.forChild(".block"));
			Optional<Container> container = block
				.flatMap(self -> Optional.ofNullable(self.entity()))
				.filter(Container.class::isInstance)
				.map(Container.class::cast);

			if (block.isPresent() && container.isEmpty()) {
				context.forChild(".block").reportProblem("Block is not a container!");
			}

			return container
				.map(self -> MiscUtil.createContainerSlotSafely(self, slot))
				.orElse(SlotAccess.NULL);

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		SlotProvider.super.validate(validator);
		block().validate(validator.forChild(".block"));
		slot().validate(validator.forChild(".slot"));
	}

}
