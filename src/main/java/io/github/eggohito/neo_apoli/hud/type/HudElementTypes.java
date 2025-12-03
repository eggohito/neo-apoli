package io.github.eggohito.neo_apoli.hud.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.BarHudElement;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class HudElementTypes {

	public static final HudElementType<BarHudElement> BAR = registerInternal("bar", BarHudElement.CODEC, BarHudElement.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <G extends HudElement> HudElementType<G> registerInternal(String path, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <G extends HudElement> HudElementType<G> register(ResourceLocation id, MapCodec<G> mapCodec, StreamCodec<RegistryFriendlyByteBuf, G> streamCodec) {
		return Registry.register(NeoApoliRegistries.HUD_ELEMENT_TYPE, id, new HudElementType<>(mapCodec, streamCodec));
	}

}
