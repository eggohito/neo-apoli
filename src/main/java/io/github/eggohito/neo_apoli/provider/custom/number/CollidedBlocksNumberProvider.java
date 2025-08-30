package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.provider.BoxProvider;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.box.EntityBoxProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypeUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.World;

@EqualsAndHashCode
@Data
public final class CollidedBlocksNumberProvider extends NumberProvider {

	public static final MapCodec<CollidedBlocksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(CollidedBlocksNumberProvider::blockCondition),
		BoxProvider.CODEC.fieldOf("box").forGetter(CollidedBlocksNumberProvider::box)
	).apply(instance, CollidedBlocksNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, CollidedBlocksNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, CollidedBlocksNumberProvider::blockCondition,
		BoxProvider.PACKET_CODEC, CollidedBlocksNumberProvider::box,
		CollidedBlocksNumberProvider::new
	);

	private final BlockCondition blockCondition;
	private final BoxProvider box;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.COLLIDED_BLOCKS;
	}

	@Override
	protected Number impl(Context context) {

		World world = context.getWorld();
		Entity entity = box() instanceof EntityBoxProvider entityBox
			? context.nullable(entityBox.entity().getParameter())
			: null;

		Box box = box().next(context.makeChild(".box"));
		BlockCollisionSpliterator<BlockPos> spliterator = new BlockCollisionSpliterator<>(world, entity, box, entity != null, (pos, shape) -> pos);

		int matches = 0;

		while (spliterator.hasNext()) {

			BlockPos blockPos = spliterator.next();
			Context blockContext = context.copy(builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BLOCK))
				.add(ContextParameters.BLOCK_POS, blockPos)
				.add(ContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

			if (blockCondition().test(blockContext.makeChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		blockCondition().validate(reporter.makeChild(".block_condition"));
		box().validate(reporter.makeChild(".box"));

	}

}
