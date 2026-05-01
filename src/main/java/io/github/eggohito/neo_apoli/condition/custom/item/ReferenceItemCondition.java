package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.condition.kind.custom.ItemConditionKind;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceItemCondition(ResourceLocation value) implements ItemCondition, ReferenceMetaCondition<ItemCondition> {

	public static final MapCodec<ReferenceItemCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceItemCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceItemCondition::new);

	@Override
	public ConditionKind<ItemCondition> targetCategory() {
		return ItemConditionKind.INSTANCE;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.REFERENCE;
	}

}
