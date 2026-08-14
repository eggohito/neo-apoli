package io.github.eggohito.neo_apoli.provider.custom.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliSlotProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.SlotAccess;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record EntitySlotProvider(EntityProvider entity, NumberProvider slot) implements SlotProvider {

	public static final MapCodec<EntitySlotProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityProvider.CODEC.fieldOf("entity").forGetter(EntitySlotProvider::entity),
		NumberProvider.CODEC.fieldOf("slot").forGetter(EntitySlotProvider::slot)
	).apply(instance, EntitySlotProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntitySlotProvider> STREAM_CODEC = StreamCodec.composite(
		EntityProvider.STREAM_CODEC, EntitySlotProvider::entity,
		NumberProvider.STREAM_CODEC, EntitySlotProvider::slot,
		EntitySlotProvider::new
	);

	@Override
	public @NotNull Type<?> getType() {
		return NeoApoliSlotProviderTypes.ENTITY;
	}

	@Override
	public Optional<SlotAccess> getSlot(Context context) {

		Context slotContext = context.forChild(".slot");
		int slot = slot().getInt(slotContext);

		if (slotContext.hasErrors()) {
			return Optional.empty();
		}

		else {
			return entity()
				.getEntity(context.forChild(".entity"))
				.map(entity -> entity.getSlot(slot));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		SlotProvider.super.validate(validator);
		entity().validate(validator.forChild(".entity"));
		slot().validate(validator.forChild(".slot"));
	}

}
