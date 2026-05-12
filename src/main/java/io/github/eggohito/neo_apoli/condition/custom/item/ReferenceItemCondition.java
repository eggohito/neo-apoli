package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceItemCondition(ResourceLocation value) implements ItemCondition, ReferenceMetaCondition<ItemCondition> {

	public static final MapCodec<ReferenceItemCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceItemCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceItemCondition::new);

	@Override
	public ItemCondition.Kind targetKind() {
		return ItemCondition.Kind.INSTANCE;
	}

	@Override
	public ItemCondition.Type<?> getType() {
		return NeoApoliItemConditionTypes.REFERENCE;
	}

}
