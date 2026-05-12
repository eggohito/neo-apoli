package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public record PlaceBlockAction(BlockInput block, Mode mode) implements BlockAction {

	public static final MapCodec<PlaceBlockAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.REGULAR_OR_STRINGIFIED_BLOCK_INPUT.fieldOf("block").forGetter(PlaceBlockAction::block),
		Mode.CODEC.optionalFieldOf("mode", Mode.DEFAULT).forGetter(PlaceBlockAction::mode)
	).apply(instance, PlaceBlockAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PlaceBlockAction> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.BLOCK_INPUT, PlaceBlockAction::block,
		Mode.STREAM_CODEC, PlaceBlockAction::mode,
		PlaceBlockAction::new
	);

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.PLACE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		BlockPos blockPos = context.getRequired(NeoApoliContextParams.BLOCK_POS);
		int flags = Block.UPDATE_CLIENTS | mode().flag;

		boolean placeBlock = true;
		boolean updateNeighbors = true;

		switch (mode()) {
			case DESTROY -> {
				serverLevel.destroyBlock(blockPos, true);
				placeBlock = !block().getState().isAir() || !serverLevel.isEmptyBlock(blockPos);
			}
			case KEEP ->
				placeBlock = serverLevel.isEmptyBlock(blockPos);
			case DEFAULT -> {

				placeBlock = serverLevel.isEmptyBlock(blockPos);
				Direction direction = context.getNullable(NeoApoliContextParams.DIRECTION);

				if (!placeBlock && direction != null) {

					blockPos = blockPos.relative(direction);
					placeBlock = serverLevel.isEmptyBlock(blockPos);

				}

			}
			case STRICT ->
				updateNeighbors = false;
		}

		if (placeBlock && block().place(serverLevel, blockPos, flags) && updateNeighbors) {
			serverLevel.updateNeighborsAt(blockPos, block().getState().getBlock());
		}

	}

	public enum Mode {

		DESTROY(Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS),
		KEEP(Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS),
		REPLACE(Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS),
		DEFAULT(Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS),
		STRICT(Block.UPDATE_SKIP_ALL_SIDEEFFECTS);

		public static final Codec<Mode> CODEC = CodecUtil.enumType(Mode.class);
		public static final StreamCodec<ByteBuf, Mode> STREAM_CODEC = StreamCodecUtil.enumType(Mode.class);

		final int flag;

		Mode(int flag) {
			this.flag = flag;
		}

	}

}
