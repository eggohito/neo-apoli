package io.github.eggohito.neo_apoli.gui;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.gui.type.GuiElementType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface GuiElement {

	Codec<GuiElement> CODEC = GuiElementType.CODEC.dispatch(GuiElement::getType, GuiElementType::mapCodec);

	Codec<GuiElement> INTEGRATING_CODEC = GuiElementType.CODEC.dispatch(GuiElement::getType, GuiElementType::integratingMapCodec);

	StreamCodec<RegistryFriendlyByteBuf, GuiElement> STREAM_CODEC = GuiElementType.STREAM_CODEC.dispatch(GuiElement::getType, GuiElementType::streamCodec);

	GuiElementType<?> getType();

	@Getter
	@Setter
	class Position {
		int x;
		int y;
	}

}
