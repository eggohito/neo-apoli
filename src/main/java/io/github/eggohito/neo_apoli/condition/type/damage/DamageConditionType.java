package io.github.eggohito.neo_apoli.condition.type.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record DamageConditionType<C extends DamageCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final String PREFIX = "damage/";

	public static final RegistryFixedAlias<DamageConditionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, ConditionType.ALIASES, PREFIX, "");

	public static final Codec<DamageConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, DamageConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.DAMAGE_CONDITION_TYPE);

}
