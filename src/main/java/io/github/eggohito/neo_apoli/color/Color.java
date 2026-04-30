package io.github.eggohito.neo_apoli.color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.color.type.ColorType;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;

/**
 * 	Color is an interface used to store ARGB data in object form. Getting the ARGB data as a primitive integer also
 * 	has access to a {@link Context}, which allows for further capabilities (e.g: using context-based objects for
 * 	generating ARGB values)
 */
public interface Color extends ContextUser {

	Codec<Color> CODEC = new MultiAlternativeCodec<>(createCodec("type"), Rgba.STRING_CODEC);
	StreamCodec<RegistryFriendlyByteBuf, Color> STREAM_CODEC = ColorType.STREAM_CODEC.dispatch(Color::getType, ColorType::streamCodec);

	ColorType<?> getType();

	int intValue(Context context);

	//	TODO: Add an parameter for determining how the ARGB colors are mixed
	static int mix(int first, int second) {
		return ARGB.multiply(first, second);
	}

	static Codec<Color> createCodec(String typeKey) {
		return createMapCodec(typeKey).codec();
	}

	static MapCodec<Color> createMapCodec(String typeKey) {
		return ColorType.CODEC.dispatchMap(typeKey, Color::getType, ColorType::mapCodec);
	}

}
