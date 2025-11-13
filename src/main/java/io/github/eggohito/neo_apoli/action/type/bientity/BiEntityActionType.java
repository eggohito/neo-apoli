package io.github.eggohito.neo_apoli.action.type.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BiEntityActionType<A extends BiEntityAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

	public static final String PREFIX = "bientity/";

	public static final RegistryFixedAlias<BiEntityActionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.BIENTITY_ACTION_TYPE, ActionType.ALIASES, PREFIX, "");

	public static final Codec<BiEntityActionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, BiEntityActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BIENTITY_ACTION_TYPE);

}
