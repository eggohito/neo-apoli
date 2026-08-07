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

import java.util.Optional;

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
	public Optional<AABB> getBox(Context context) {

		Context minContext = context.forChild(".min");
		Vec3 min = min().getVec3(minContext);

		if (minContext.hasErrors()) {
			return Optional.empty();
		}

		Context maxContext = context.forChild(".max");
		Vec3 max = max().getVec3(maxContext);

		if (maxContext.hasErrors()) {
			return Optional.empty();
		}

		return Optional.of(new AABB(min, max));

	}

	@Override
	public void validate(Context.Validator validator) {

		BoxProvider.super.validate(validator);

		min().validate(validator.forChild(".min"));
		max().validate(validator.forChild(".max"));

	}

}
