package io.github.eggohito.neo_apoli.condition.type.key;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.key.KeyCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record KeyConditionType<C extends KeyCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final String PREFIX = "key/";

	public static final RegistryFixedAlias<KeyConditionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.KEY_CONDITION_TYPE, ConditionType.ALIASES, PREFIX, "");

	public static final Codec<KeyConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, KeyConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.KEY_CONDITION_TYPE);

}
