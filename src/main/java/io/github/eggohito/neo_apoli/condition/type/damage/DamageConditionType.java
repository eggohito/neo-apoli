package io.github.eggohito.neo_apoli.condition.type.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DamageConditionType<C extends DamageCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) implements ConditionType<C> {

	public static final String PREFIX = "damage/";

	public static final RegistryFixedAlias<DamageConditionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, ConditionType.ALIASES, PREFIX, "");

	public static final Codec<DamageConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.DAMAGE_CONDITION_TYPE);

}
