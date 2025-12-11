package io.github.eggohito.neo_apoli.hud.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.NauseaOverlayHudElement;
import io.github.eggohito.neo_apoli.hud.custom.ResourceBarHudElement;
import io.github.eggohito.neo_apoli.hud.custom.TextureOverlayHudElement;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class HudElementTypes {

	public static final HudElementType<NauseaOverlayHudElement> NAUSEA_OVERLAY = registerInternal("overlay/nausea", NauseaOverlayHudElement.CODEC, NauseaOverlayHudElement.STREAM_CODEC);
	public static final HudElementType<ResourceBarHudElement> RESOURCE_BAR = registerInternal("resource_bar", ResourceBarHudElement.CODEC, ResourceBarHudElement.STREAM_CODEC);
	public static final HudElementType<TextureOverlayHudElement> TEXTURE_OVERLAY = registerInternal("overlay/texture", TextureOverlayHudElement.CODEC, TextureOverlayHudElement.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <G extends HudElement> HudElementType<G> registerInternal(String path, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <G extends HudElement> HudElementType<G> register(ResourceLocation id, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return Registry.register(NeoApoliRegistries.HUD_ELEMENT_TYPE, id, new HudElementType<>(mapCodec, streamCodec));
	}

}
