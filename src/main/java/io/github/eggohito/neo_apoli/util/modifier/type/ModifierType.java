package io.github.eggohito.neo_apoli.util.modifier.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ModifierType<M extends Modifier>(MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> packetCodec) {

	public static final RegistryFixedAlias<ModifierType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.MODIFIER_TYPE);

	public static final Codec<ModifierType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifierType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.MODIFIER_TYPE);

}
