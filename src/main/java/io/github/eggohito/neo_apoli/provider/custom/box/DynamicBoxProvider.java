package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record DynamicBoxProvider(Vec3dProvider min, Vec3dProvider max) implements BoxProvider {

	public static final MapCodec<DynamicBoxProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("min").forGetter(DynamicBoxProvider::min),
		Vec3dProvider.CODEC.fieldOf("max").forGetter(DynamicBoxProvider::max)
	).apply(instance, DynamicBoxProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBoxProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3dProvider.STREAM_CODEC, DynamicBoxProvider::min,
		Vec3dProvider.STREAM_CODEC, DynamicBoxProvider::max,
		DynamicBoxProvider::new
	);

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.DYNAMIC;
	}

	@Override
	public @NotNull AABB next(Context context) {

		Vec3 min = min().next(context.makeChild(".min"));
		Vec3 max = max().next(context.makeChild(".max"));

		return new AABB(min, max);

	}

	@Override
	public void validate(ProblemReporter reporter) {

		BoxProvider.super.validate(reporter);

		min().validate(reporter.forChild(".min"));
		max().validate(reporter.forChild(".max"));

	}

}
