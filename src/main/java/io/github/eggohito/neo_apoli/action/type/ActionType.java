package io.github.eggohito.neo_apoli.action.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ActionType<A extends Action> {

	RegistryFixedAlias<ActionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.ACTION_TYPE);

	Codec<ActionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	PacketCodec<RegistryByteBuf, ActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ACTION_TYPE);

	MapCodec<A> mapCodec();

	PacketCodec<RegistryByteBuf, A> packetCodec();

}
