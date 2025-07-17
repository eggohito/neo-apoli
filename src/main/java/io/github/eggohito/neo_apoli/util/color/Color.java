package io.github.eggohito.neo_apoli.util.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.util.color.type.ColorType;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface Color {

	Codec<Color> CODEC = new MultiAlternativeCodec<>(createCodec("type"), Rgba.STRING_CODEC);
	PacketCodec<RegistryByteBuf, Color> PACKET_CODEC = ColorTypes.PACKET_CODEC.dispatch(Color::type, ColorType::packetCodec);

	ColorType<?> type();

	default Argb toArgb(Context context) {
		return toArgb();
	}

	Argb toArgb();

	static Codec<Color> createCodec(String typeKey) {
		return createMapCodec(typeKey).codec();
	}

	static MapCodec<Color> createMapCodec(String typeKey) {
		return ColorTypes.CODEC.dispatchMap(typeKey, Color::type, ColorType::mapCodec);
	}

}
