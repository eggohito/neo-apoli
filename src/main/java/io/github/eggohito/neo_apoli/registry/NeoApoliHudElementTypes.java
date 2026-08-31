package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.hud.element.HudElement;
import io.github.eggohito.neo_apoli.hud.element.custom.NauseaOverlayHudElement;
import io.github.eggohito.neo_apoli.hud.element.custom.ResourceBarHudElement;
import io.github.eggohito.neo_apoli.hud.element.custom.TextureOverlayHudElement;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class NeoApoliHudElementTypes {

	public static final HudElement.Type<NauseaOverlayHudElement> NAUSEA_OVERLAY = registerInternal("overlay/nausea", NauseaOverlayHudElement.CODEC, NauseaOverlayHudElement.STREAM_CODEC);
	public static final HudElement.Type<ResourceBarHudElement> RESOURCE_BAR = registerInternal("resource_bar", ResourceBarHudElement.CODEC, ResourceBarHudElement.STREAM_CODEC);
	public static final HudElement.Type<TextureOverlayHudElement> TEXTURE_OVERLAY = registerInternal("overlay/texture", TextureOverlayHudElement.CODEC, TextureOverlayHudElement.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <G extends HudElement> HudElement.Type<G> registerInternal(String path, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <G extends HudElement> HudElement.Type<G> register(ResourceLocation id, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return Registry.register(NeoApoliRegistries.HUD_ELEMENT_TYPE, id, new HudElement.Type<>(mapCodec, streamCodec));
	}

}
