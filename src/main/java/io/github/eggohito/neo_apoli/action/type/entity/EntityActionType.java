package io.github.eggohito.neo_apoli.action.type.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record EntityActionType<A extends EntityAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

	public static final String PREFIX = "entity/";

	public static final RegistryFixedAlias<EntityActionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.ENTITY_ACTION_TYPE, ActionType.ALIASES, PREFIX, "");

	public static final Codec<EntityActionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, EntityActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ENTITY_ACTION_TYPE);


}
