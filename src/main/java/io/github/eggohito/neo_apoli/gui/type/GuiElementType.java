package io.github.eggohito.neo_apoli.gui.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.gui.GuiElement;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GuiElementType<G extends GuiElement>(MapCodec<G> mapCodec, MapCodec<G> integratingMapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {

	public static final RegistryFixedAlias<GuiElementType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.GUI_ELEMENT_TYPE);

	public static final Codec<GuiElementType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, GuiElementType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.GUI_ELEMENT_TYPE);

}
