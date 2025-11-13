package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record TranslateBoxProvider(BoxProvider box, Vec3dProvider translation) implements BoxProvider {

	public static final MapCodec<TranslateBoxProvider> CODEC = MapCodecUtil.lazy(TranslateBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BoxProvider.CODEC.fieldOf("box").forGetter(TranslateBoxProvider::box),
		Vec3dProvider.CODEC.fieldOf("translation").forGetter(TranslateBoxProvider::translation)
	).apply(instance, TranslateBoxProvider::new)));

	public static final PacketCodec<RegistryByteBuf, TranslateBoxProvider> PACKET_CODEC = PacketCodecUtil.lazy(TranslateBoxProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		BoxProvider.PACKET_CODEC, TranslateBoxProvider::box,
		Vec3dProvider.PACKET_CODEC, TranslateBoxProvider::translation,
		TranslateBoxProvider::new
	));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.TRANSLATE;
	}

	@Override
	public @NotNull Box next(Context context) {

		Box box = box().next(context.makeChild(".box"));
		Vec3d translation = translation().next(context.makeChild(".translation"));

		return new Box(translation.subtract(box.getMinPos()), translation.add(box.getMaxPos()));

	}

	@Override
	public void validate(ErrorReporter reporter) {

		BoxProvider.super.validate(reporter);

		box().validate(reporter.makeChild(".box"));
		translation().validate(reporter.makeChild(".translation"));

	}

}
