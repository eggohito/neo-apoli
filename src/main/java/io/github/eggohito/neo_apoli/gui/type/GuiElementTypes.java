package io.github.eggohito.neo_apoli.gui.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.gui.GuiElement;
import io.github.eggohito.neo_apoli.gui.custom.BarGuiElement;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class GuiElementTypes {

	public static final GuiElementType<BarGuiElement> BAR = registerInternal("bar", BarGuiElement.CODEC, BarGuiElement.INTEGRATING_CODEC, BarGuiElement.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <G extends GuiElement> GuiElementType<G> registerInternal(String path, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <G extends GuiElement> GuiElementType<G> registerInternal(String path, MapCodec<G> mapCodec, MapCodec<G> integratingMapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, integratingMapCodec, streamCodec);
	}

	public static <G extends GuiElement> GuiElementType<G> register(ResourceLocation id, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return register(id, mapCodec, MapCodecUtil.fail(() -> "GUI element type \"" + id + "\" cannot be integrated!"), streamCodec);
	}

	public static <G extends GuiElement> GuiElementType<G> register(ResourceLocation id, MapCodec<G> mapCodec, MapCodec<G> integratingMapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return Registry.register(NeoApoliRegistries.GUI_ELEMENT_TYPE, id, new GuiElementType<>(mapCodec, integratingMapCodec, streamCodec));
	}

}
