package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record BoxComponentNumberProvider(BoxProvider box, Direction side) implements NumberProvider {

	public static final MapCodec<BoxComponentNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BoxProvider.CODEC.fieldOf("box").forGetter(BoxComponentNumberProvider::box),
		Direction.CODEC.fieldOf("side").forGetter(BoxComponentNumberProvider::side)
	).apply(instance, BoxComponentNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BoxComponentNumberProvider> STREAM_CODEC = StreamCodec.composite(
		BoxProvider.STREAM_CODEC, BoxComponentNumberProvider::box,
		Direction.STREAM_CODEC, BoxComponentNumberProvider::side,
		BoxComponentNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.BOX_COMPONENT;
	}

	@Override
	public @NotNull Number nextNumber(Context context) {

		Context boxContext = context.forChild(".box");
		AABB box = box().nextBox(boxContext);

		if (boxContext.hasErrors()) {
			return 0.0;
		}

		return switch (side()) {
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

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		box().validate(validator.forChild(".box"));
	}

}
