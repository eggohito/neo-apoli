package io.github.eggohito.neo_apoli.condition.type.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BiEntityConditionType<C extends BiEntityCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final RegistryAlias<BiEntityConditionType<?>> ALIASES = new RegistryAlias<>(NeoApoliRegistries.BIENTITY_CONDITION_TYPE);

	public static final Codec<BiEntityConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);
	public static final PacketCodec<RegistryByteBuf, BiEntityConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BIENTITY_CONDITION_TYPE);

}
