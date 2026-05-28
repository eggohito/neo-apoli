package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBoxProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record DynamicBoxProvider(Vec3Provider min, Vec3Provider max) implements BoxProvider {

	public static final MapCodec<DynamicBoxProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("min").forGetter(DynamicBoxProvider::min),
		Vec3Provider.CODEC.fieldOf("max").forGetter(DynamicBoxProvider::max)
	).apply(instance, DynamicBoxProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBoxProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, DynamicBoxProvider::min,
		Vec3Provider.STREAM_CODEC, DynamicBoxProvider::max,
		DynamicBoxProvider::new
	);

	@Override
	public @NotNull BoxProvider.Type<?> getType() {
		return NeoApoliBoxProviderTypes.DYNAMIC;
	}

	@Override
	public @NotNull AABB nextBox(Context context) {

		Vec3 min = min().getVec3(context.forChild(".min"));
		Vec3 max = max().getVec3(context.forChild(".max"));

		return new AABB(min, max);

	}

	@Override
	public void validate(Context.Validator validator) {

		BoxProvider.super.validate(validator);

		min().validate(validator.forChild(".min"));
		max().validate(validator.forChild(".max"));

	}

}
