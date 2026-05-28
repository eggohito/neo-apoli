package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.exception.PosOutOfBoundsException;
import io.github.eggohito.neo_apoli.exception.PosUnloadedException;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AdjacentBlocksNumberProvider(Condition condition, Vec3Provider position) implements NumberProvider {

	public static final Context.Parameter<CachedBlock> ADJACENT_BLOCK = NeoApoliContextParams.registerSimpleInternal("adjacent_block", CachedBlock.class);
	public static final ContextKeySet CONDITION_PARAMETER_SET = new ContextKeySet.Builder().required(ADJACENT_BLOCK).build();

	public static final MapCodec<AdjacentBlocksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.fieldOf("condition").forGetter(AdjacentBlocksNumberProvider::condition),
		Vec3Provider.CODEC.fieldOf("position").forGetter(AdjacentBlocksNumberProvider::position)
	).apply(instance, AdjacentBlocksNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdjacentBlocksNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Condition.STREAM_CODEC, AdjacentBlocksNumberProvider::condition,
		Vec3Provider.STREAM_CODEC, AdjacentBlocksNumberProvider::position,
		AdjacentBlocksNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ADJACENT_BLOCKS;
	}

	@Override
	public double getDouble(Context context) {

		Level level = context.level();
		long matches = 0;

		Context positionContext = context.forChild(".position");
		BlockPos pos = BlockPos.containing(position().getVec3(positionContext));

		if (positionContext.hasErrors()) {
			return matches;
		}

		for (var direction : Direction.values()) {

			try {

				CachedBlock block = CachedBlock.fromLoadedPos(level, pos.relative(direction));
				Context blockContext = new Context.Builder(context)
					.withRequired(ADJACENT_BLOCK, block)
					.build(level);

				if (condition().test(blockContext.forChild(".condition"))) {
					matches++;
				}

			}

			catch (PosUnloadedException | PosOutOfBoundsException ignored) {
				//  No-op
			}

		}

		return matches;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMETER_SET).forChild(".condition"));
		position().validate(validator.forChild(".position"));
	}


}
