package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BlockActionType<A extends BlockAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

	public static final String PREFIX = "block/";

	public static final RegistryFixedAlias<BlockActionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.BLOCK_ACTION_TYPE, ActionType.ALIASES, PREFIX, "");

	public static final Codec<BlockActionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, BlockActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BLOCK_ACTION_TYPE);

}
