package io.github.eggohito.neo_apoli.condition.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ConditionType<C extends Condition> {

	RegistryFixedAlias<ConditionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.CONDITION_TYPE);

	Codec<ConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	PacketCodec<RegistryByteBuf, ConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.CONDITION_TYPE);

	MapCodec<C> mapCodec();

	PacketCodec<RegistryByteBuf, C> packetCodec();

}
