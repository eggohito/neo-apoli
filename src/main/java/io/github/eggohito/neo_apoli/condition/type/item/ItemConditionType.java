package io.github.eggohito.neo_apoli.condition.type.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ItemConditionType<C extends ItemCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final String PREFIX = "item/";

	public static final RegistryFixedAlias<ItemConditionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.ITEM_CONDITION_TYPE, ConditionType.ALIASES, PREFIX, "");

	public static final Codec<ItemConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ITEM_CONDITION_TYPE);

}
