package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record PlaceBlockAction(BlockState block, Mode mode) implements BlockAction {

	public static final MapCodec<PlaceBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.REGULAR_OR_STRINGIFIED_BLOCK_STATE.fieldOf("block").forGetter(PlaceBlockAction::block),
		Mode.CODEC.optionalFieldOf("mode", Mode.DEFAULT).forGetter(PlaceBlockAction::mode)
	).apply(instance, PlaceBlockAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PlaceBlockAction> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.BLOCK_STATE, PlaceBlockAction::block,
		Mode.STREAM_CODEC, PlaceBlockAction::mode,
		PlaceBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.PLACE;
	}

	@Override
	public void serverExecute(ServerContext context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		ServerLevel world = context.getWorld();
		BlockPos blockPos = context.required(NeoApoliContextKeys.BLOCK_POS);

		int flags = Block.UPDATE_CLIENTS | mode().flag;

		boolean placeBlock = true;
		boolean updateNeighbors = true;

		switch (mode()) {
			case DESTROY -> {
				world.destroyBlock(blockPos, true);
				placeBlock = !block().isAir() || !world.isEmptyBlock(blockPos);
			}
			case KEEP ->
				placeBlock = world.isEmptyBlock(blockPos);
			case DEFAULT -> {

				placeBlock = world.isEmptyBlock(blockPos);
				Direction direction = context.nullable(NeoApoliContextKeys.DIRECTION);

				if (!placeBlock && direction != null) {

					blockPos = blockPos.relative(direction);
					placeBlock = world.isEmptyBlock(blockPos);

				}

			}
			case STRICT ->
				updateNeighbors = false;
		}

		if (placeBlock && world.setBlock(blockPos, block(), flags) && updateNeighbors) {
			world.updateNeighborsAt(blockPos, block().getBlock());
		}

	}

	public enum Mode implements StringRepresentable {

		DESTROY("destroy"),
		KEEP("keep"),
		REPLACE("replace"),
		DEFAULT("default"),
		STRICT("strict", Block.UPDATE_SKIP_ALL_SIDEEFFECTS);

		public static final Codec<Mode> CODEC = CodecUtil.enumType(Mode.class);
		public static final StreamCodec<ByteBuf, Mode> STREAM_CODEC = StreamCodecUtil.enumType(Mode.class);

		final String id;
		final int flag;

		Mode(String id, int flag) {
			this.id = id;
			this.flag = flag;
		}

		Mode(String id) {
			this(id, Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS);
		}

		@Override
		public String getSerializedName() {
			return id;
		}

	}

}
