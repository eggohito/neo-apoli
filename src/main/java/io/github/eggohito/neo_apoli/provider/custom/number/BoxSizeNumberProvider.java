package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record BoxSizeNumberProvider(BoxProvider box) implements NumberProvider {

	public static final MapCodec<BoxSizeNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BoxProvider.CODEC.fieldOf("box").forGetter(BoxSizeNumberProvider::box))
		.apply(instance, BoxSizeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BoxSizeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		BoxProvider.STREAM_CODEC, BoxSizeNumberProvider::box,
		BoxSizeNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.BOX_SIZE;
	}

	@Override
	public @NotNull Number nextNumber(Context context) {

		Context boxContext = context.forChild(".box");
		AABB box = box().nextBox(boxContext);

		if (boxContext.hasErrors()) {
			return 0.0D;
		}

		else {
			return box.getSize();
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		box().validate(validator.forChild(".box"));
	}

}
