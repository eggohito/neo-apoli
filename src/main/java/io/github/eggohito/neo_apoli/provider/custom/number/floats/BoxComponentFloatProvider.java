package io.github.eggohito.neo_apoli.provider.custom.number.floats;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.custom.direction.DirectionProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliFloatProviderTypes;
import io.github.eggohito.neo_apoli.util.FloatConsumer;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record BoxComponentFloatProvider(BoxProvider box, DirectionProvider side) implements FloatProvider {

	public static final MapCodec<BoxComponentFloatProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BoxProvider.CODEC.fieldOf("box").forGetter(BoxComponentFloatProvider::box),
		DirectionProvider.CODEC.fieldOf("side").forGetter(BoxComponentFloatProvider::side)
	).apply(instance, BoxComponentFloatProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BoxComponentFloatProvider> STREAM_CODEC = StreamCodec.composite(
		BoxProvider.STREAM_CODEC, BoxComponentFloatProvider::box,
		DirectionProvider.STREAM_CODEC, BoxComponentFloatProvider::side,
		BoxComponentFloatProvider::new
	);

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.BOX_COMPONENT;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {

		AABB box = box()
			.getBox(context.forChild(".box"))
			.orElse(null);

		if (box == null) {
			return;
		}

		Direction side = side()
			.getDirection(context.forChild(".side"))
			.orElse(null);

		if (side == null) {
			return;
		}

		double component = switch (side) {
			case DOWN ->
				box.minY;
			case UP ->
				box.maxY;
			case NORTH ->
				box.minZ;
			case SOUTH ->
				box.maxZ;
			case WEST ->
				box.minX;
			case EAST ->
				box.maxX;
		};

		setter.accept((float) component);

	}

	@Override
	public void validate(Context.Validator validator) {
		FloatProvider.super.validate(validator);
		box().validate(validator.forChild(".box"));
		side().validate(validator.forChild(".side"));
	}

}
