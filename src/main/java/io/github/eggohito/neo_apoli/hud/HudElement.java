package io.github.eggohito.neo_apoli.hud;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.hud.type.HudElementType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface HudElement {

	Codec<HudElement> CODEC = HudElementType.CODEC.dispatch(HudElement::getType, HudElementType::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, HudElement> STREAM_CODEC = HudElementType.STREAM_CODEC.dispatch(HudElement::getType, HudElementType::streamCodec);

	HudElementType<?> getType();

	@Getter
	@Setter
	class Position {
		int x;
		int y;
	}

}
