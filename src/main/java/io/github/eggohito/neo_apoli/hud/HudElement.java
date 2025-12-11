package io.github.eggohito.neo_apoli.hud;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.hud.type.HudElementType;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface HudElement extends ContextAware {

	Codec<HudElement> CODEC = HudElementType.CODEC.dispatch(HudElement::getType, HudElementType::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, HudElement> STREAM_CODEC = HudElementType.STREAM_CODEC.dispatch(HudElement::getType, HudElementType::streamCodec);

	HudElementType<?> getType();

	int order();

	default boolean shouldRender(Context context, HudRenderPhase renderPhase) {
		return renderPhase == HudRenderPhase.ABOVE_HUD;
	}

	default boolean hideWithHud(Context context) {
		return true;
	}

	@Getter
	@Setter
	class Position {
		int x;
		int y;
	}

}
