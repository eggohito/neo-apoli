package io.github.eggohito.neo_apoli.condition.type.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BlockConditionType<C extends BlockCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final String PREFIX = "block/";

	public static final RegistryFixedAlias<BlockConditionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.BLOCK_CONDITION_TYPE, ConditionType.ALIASES, PREFIX, "");

	public static final Codec<BlockConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, BlockConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BLOCK_CONDITION_TYPE);

}
