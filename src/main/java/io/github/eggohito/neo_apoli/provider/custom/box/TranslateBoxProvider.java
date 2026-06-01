package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBoxProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record TranslateBoxProvider(BoxProvider box, Vec3Provider translation) implements BoxProvider {

	public static final MapCodec<TranslateBoxProvider> MAP_CODEC = MapCodecUtil.lazy(TranslateBoxProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BoxProvider.CODEC.fieldOf("box").forGetter(TranslateBoxProvider::box),
		Vec3Provider.CODEC.fieldOf("translation").forGetter(TranslateBoxProvider::translation)
	).apply(instance, TranslateBoxProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, TranslateBoxProvider> STREAM_CODEC = StreamCodecUtil.lazy(TranslateBoxProvider.class.getSimpleName(), () -> StreamCodec.composite(
		BoxProvider.STREAM_CODEC, TranslateBoxProvider::box,
		Vec3Provider.STREAM_CODEC, TranslateBoxProvider::translation,
		TranslateBoxProvider::new
	));

	@Override
	public @NotNull BoxProvider.Type<?> getType() {
		return NeoApoliBoxProviderTypes.TRANSLATE;
	}

	@Override
	public @NotNull AABB getBox(Context context) {

		AABB box = box().getBox(context.forChild(".box"));
		Vec3 translation = translation().getVec3(context.forChild(".translation"));

		return new AABB(translation.subtract(box.getMinPosition()), translation.add(box.getMaxPosition()));

	}

	@Override
	public void validate(Context.Validator validator) {

		BoxProvider.super.validate(validator);

		box().validate(validator.forChild(".box"));
		translation().validate(validator.forChild(".translation"));

	}

}
