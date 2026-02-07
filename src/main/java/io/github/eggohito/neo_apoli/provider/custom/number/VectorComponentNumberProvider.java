package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record VectorComponentNumberProvider(Vec3Provider vector, Direction.Axis axis) implements NumberProvider {

	public static final MapCodec<VectorComponentNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("vector").forGetter(VectorComponentNumberProvider::vector),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(VectorComponentNumberProvider::axis)
	).apply(instance, VectorComponentNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, VectorComponentNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, VectorComponentNumberProvider::vector,
		NeoApoliStreamCodecs.AXIS, VectorComponentNumberProvider::axis,
		VectorComponentNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.VECTOR_COMPONENT;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context vectorContext = context.forChild(".vector");
		Vec3 vector = vector().next(vectorContext);

		if (vectorContext.hasErrors()) {
			return 0.0d;
		}

		else {
			return vector.get(this.axis());
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		vector().validate(validator.forChild(".vector"));
	}

}
