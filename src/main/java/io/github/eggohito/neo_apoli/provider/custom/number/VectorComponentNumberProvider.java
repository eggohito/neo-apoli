package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record VectorComponentNumberProvider(Vec3Provider vector, Direction.Axis axis) implements NumberProvider {

	public static final MapCodec<VectorComponentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("vector").forGetter(VectorComponentNumberProvider::vector),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(VectorComponentNumberProvider::axis)
	).apply(instance, VectorComponentNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, VectorComponentNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, VectorComponentNumberProvider::vector,
		NeoApoliStreamCodecs.AXIS, VectorComponentNumberProvider::axis,
		VectorComponentNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.VECTOR_COMPONENT;
	}

	@Override
	public double getDouble(Context context) {
		return vector().getVec3(context.forChild(".vector"))
			.map(vector -> vector.get(this.axis()))
			.orElse(0.0D);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		vector().validate(validator.forChild(".vector"));
	}

}
