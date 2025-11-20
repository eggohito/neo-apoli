package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Set;

public record PlaceBlockAction(BlockState block, Mode mode) implements BlockAction {

	public static final MapCodec<PlaceBlockAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.REGULAR_OR_STRINGIFIED_BLOCK_STATE.fieldOf("block").forGetter(PlaceBlockAction::block),
		Mode.CODEC.optionalFieldOf("mode", Mode.DEFAULT).forGetter(PlaceBlockAction::mode)
	).apply(instance, PlaceBlockAction::new));

	public static final PacketCodec<RegistryByteBuf, PlaceBlockAction> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.BLOCK_STATE, PlaceBlockAction::block,
		Mode.PACKET_CODEC, PlaceBlockAction::mode,
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

		ServerWorld world = context.getWorld();
		BlockPos blockPos = context.required(NeoApoliContextParameters.BLOCK_POS);

		int flags = Block.NOTIFY_LISTENERS | mode().flag;

		boolean placeBlock = true;
		boolean updateNeighbors = true;

		switch (mode()) {
			case DESTROY -> {
				world.breakBlock(blockPos, true);
				placeBlock = !block().isAir() || !world.isAir(blockPos);
			}
			case KEEP ->
				placeBlock = world.isAir(blockPos);
			case DEFAULT -> {

				placeBlock = world.isAir(blockPos);
				Direction direction = context.nullable(NeoApoliContextParameters.DIRECTION);

				if (!placeBlock && direction != null) {

					blockPos = blockPos.offset(direction);
					placeBlock = world.isAir(blockPos);

				}

			}
			case STRICT ->
				updateNeighbors = false;
		}

		if (placeBlock && world.setBlockState(blockPos, block(), flags) && updateNeighbors) {
			world.updateNeighbors(blockPos, block().getBlock());
		}

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.BLOCK_POS);
	}

	public enum Mode implements StringIdentifiable {

		DESTROY("destroy"),
		KEEP("keep"),
		REPLACE("replace"),
		DEFAULT("default"),
		STRICT("strict", Block.FORCE_STATE_AND_SKIP_CALLBACKS_AND_DROPS);

		public static final Codec<Mode> CODEC = CodecUtil.enumType(Mode.class);
		public static final PacketCodec<ByteBuf, Mode> PACKET_CODEC = PacketCodecUtil.enumType(Mode.class);

		final String id;
		final int flag;

		Mode(String id, int flag) {
			this.id = id;
			this.flag = flag;
		}

		Mode(String id) {
			this(id, Block.SKIP_BLOCK_ENTITY_REPLACED_CALLBACK);
		}

		@Override
		public String asString() {
			return id;
		}

	}

}
