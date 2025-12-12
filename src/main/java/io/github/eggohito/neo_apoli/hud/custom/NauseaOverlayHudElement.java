package io.github.eggohito.neo_apoli.hud.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.hud.OverlayHudElement;
import io.github.eggohito.neo_apoli.hud.type.HudElementType;
import io.github.eggohito.neo_apoli.hud.type.HudElementTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.color.Color;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

//	TODO: Replace sprite ID with a record that accepts an atlas texture ID and the the sprite ID
public record NauseaOverlayHudElement(ResourceLocation sprite, Color color, HudRenderPhase renderPhase, BooleanProvider shouldRender, BooleanProvider hideWithHud, BooleanProvider visibleInThirdPerson, int order) implements OverlayHudElement {

	public static final MapCodec<NauseaOverlayHudElement> CODEC = OverlayHudElement.createCommonOverlayCodec(NauseaOverlayHudElement::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, NauseaOverlayHudElement> STREAM_CODEC = OverlayHudElement.createCommonOverlayStreamCodec(NauseaOverlayHudElement::new);

	@Override
	public HudElementType<?> getType() {
		return HudElementTypes.NAUSEA_OVERLAY;
	}

}
