package io.github.eggohito.neo_apoli.condition.type.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ItemConditionType<C extends ItemCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements ConditionType<C> {

	public static final String PREFIX = "item/";

	public static final FixedRegistryAlias<ItemConditionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.ITEM_CONDITION_TYPE, ConditionType.ALIASES);

	public static final Codec<ItemConditionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ITEM_CONDITION_TYPE);

}
