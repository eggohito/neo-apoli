package io.github.eggohito.neo_apoli.provider.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.exception.PosOutOfBoundsException;
import io.github.eggohito.neo_apoli.exception.PosUnloadedException;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBlockProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record WorldBlockProvider(Vec3Provider position) implements BlockProvider {

	public static final MapCodec<WorldBlockProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Vec3Provider.CODEC.fieldOf("position").forGetter(WorldBlockProvider::position))
		.apply(instance, WorldBlockProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, WorldBlockProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, WorldBlockProvider::position,
		WorldBlockProvider::new
	);

	@Override
	public BlockProvider.Type<?> getType() {
		return NeoApoliBlockProviderTypes.WORLD;
	}

	@Override
	public Optional<CachedBlock> getBlock(Context context) {

		try {

			Context positionContext = context.forChild(".position");
			Vec3 position = position().getVec3(positionContext);

			if (!positionContext.hasErrors()) {
				return Optional.of(CachedBlock.fromLoadedPos(context.level(), BlockPos.containing(position)));
			}

		}

		catch (PosUnloadedException | PosOutOfBoundsException ignored) {
			//  No-op
		}

		return Optional.empty();

	}

	@Override
	public void validate(Context.Validator validator) {
		BlockProvider.super.validate(validator);
		position().validate(validator.forChild(".position"));
	}

}
