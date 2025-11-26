package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record TranslateBoxProvider(BoxProvider box, Vec3dProvider translation) implements BoxProvider {

	public static final MapCodec<TranslateBoxProvider> CODEC = MapCodecUtil.lazy(TranslateBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BoxProvider.CODEC.fieldOf("box").forGetter(TranslateBoxProvider::box),
		Vec3dProvider.CODEC.fieldOf("translation").forGetter(TranslateBoxProvider::translation)
	).apply(instance, TranslateBoxProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, TranslateBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(TranslateBoxProvider.class.getSimpleName(), () -> StreamCodec.composite(
		BoxProvider.STREAM_CODEC, TranslateBoxProvider::box,
		Vec3dProvider.STREAM_CODEC, TranslateBoxProvider::translation,
		TranslateBoxProvider::new
	));

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.TRANSLATE;
	}

	@Override
	public @NotNull AABB next(Context context) {

		AABB box = box().next(context.makeChild(".box"));
		Vec3 translation = translation().next(context.makeChild(".translation"));

		return new AABB(translation.subtract(box.getMinPosition()), translation.add(box.getMaxPosition()));

	}

	@Override
	public void validate(ProblemReporter reporter) {

		BoxProvider.super.validate(reporter);

		box().validate(reporter.forChild(".box"));
		translation().validate(reporter.forChild(".translation"));

	}

}
