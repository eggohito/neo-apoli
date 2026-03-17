package io.github.eggohito.neo_apoli.condition.type.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WorldConditionType<C extends WorldCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements ConditionType<C> {

	public static final String PREFIX = "world/";

	public static final FixedRegistryAlias<WorldConditionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.WORLD_CONDITION_TYPE, ConditionType.ALIASES);

	public static final Codec<WorldConditionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, WorldConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.WORLD_CONDITION_TYPE);

}
