package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfItemCondition(List<ItemCondition> conditions) implements ItemCondition, AllOfMetaCondition<ItemCondition> {

	public static final MapCodec<AllOfItemCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(ItemCondition.CODEC, AllOfItemCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfItemCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(ItemCondition.STREAM_CODEC, AllOfItemCondition::new));

	@Override
	public ItemCondition.Type<?> getType() {
		return NeoApoliItemConditionTypes.ALL_OF;
	}

}
