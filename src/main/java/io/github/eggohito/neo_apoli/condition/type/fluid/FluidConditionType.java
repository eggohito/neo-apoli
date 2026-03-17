package io.github.eggohito.neo_apoli.condition.type.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.fluid.FluidCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FluidConditionType<C extends FluidCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements ConditionType<C> {

	public static final String PREFIX = "fluid/";

	public static final FixedRegistryAlias<FluidConditionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.FLUID_CONDITION_TYPE, ConditionType.ALIASES);

	public static final Codec<FluidConditionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.FLUID_CONDITION_TYPE);

}
