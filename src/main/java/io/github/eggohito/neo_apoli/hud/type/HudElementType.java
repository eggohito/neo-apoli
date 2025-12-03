package io.github.eggohito.neo_apoli.hud.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HudElementType<G extends HudElement>(MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {

	public static final RegistryFixedAlias<HudElementType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.HUD_ELEMENT_TYPE);

	public static final Codec<HudElementType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, HudElementType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.HUD_ELEMENT_TYPE);

}
