package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceItemCondition(ResourceLocation value) implements ItemCondition, IReferenceMetaCondition<ItemCondition> {

	public static final MapCodec<ReferenceItemCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceItemCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceItemCondition::new);

	@Override
	public Pair<Class<ItemCondition>, String> classAndName() {
		return Pair.of(ItemCondition.class, "Item condition");
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}
