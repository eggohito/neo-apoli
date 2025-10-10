package io.github.eggohito.neo_apoli.condition.type.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record EntityConditionType<C extends EntityCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final RegistryAlias<EntityConditionType<?>> ALIASES = new RegistryAlias<>(NeoApoliRegistries.ENTITY_CONDITION_TYPE);

	public static final Codec<EntityConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);
	public static final PacketCodec<RegistryByteBuf, EntityConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);

}
