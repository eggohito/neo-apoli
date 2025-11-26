package io.github.eggohito.neo_apoli.condition.type.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BiEntityConditionType<C extends BiEntityCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final String PREFIX = "bientity/";

	public static final RegistryFixedAlias<BiEntityConditionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, ConditionType.ALIASES, PREFIX, "");

	public static final Codec<BiEntityConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final StreamCodec<RegistryFriendlyByteBuf, BiEntityConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.BIENTITY_CONDITION_TYPE);

}
