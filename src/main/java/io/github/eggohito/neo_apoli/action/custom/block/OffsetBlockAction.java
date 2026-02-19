package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record OffsetBlockAction(BlockAction action, Vec3Provider offset) implements BlockAction {

	public static final MapCodec<OffsetBlockAction> MAP_CODEC = MapCodecUtil.lazy(OffsetBlockAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("action").forGetter(OffsetBlockAction::action),
		Vec3Provider.CODEC.fieldOf("offset").forGetter(OffsetBlockAction::offset)
	).apply(instance, OffsetBlockAction::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetBlockAction> STREAM_CODEC = StreamCodec.composite(
		BlockAction.STREAM_CODEC, OffsetBlockAction::action,
		Vec3Provider.STREAM_CODEC, OffsetBlockAction::offset,
		OffsetBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.OFFSET;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		BlockPos blockPos = context.getRequired(NeoApoliContextParams.BLOCK_POS);
		Vec3 offset = offset().nextVec3(context.forChild(".offset"));

		BlockPos offsetBlockPos = BlockPos.containing(blockPos
			.getCenter()
			.add(offset));

		if (!serverLevel.hasChunkAt(offsetBlockPos)) {
			return;
		}

		Context blockContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.BLOCK_POS, offsetBlockPos)
			.withRequired(NeoApoliContextParams.BLOCK_STATE, serverLevel.getBlockState(offsetBlockPos))
			.withNullable(NeoApoliContextParams.BLOCK_ENTITY, serverLevel.getBlockEntity(offsetBlockPos))
			.build(serverLevel);

		action().execute(blockContext.forChild(".action"));

	}

	@Override
	public void validate(Context.Validator validator) {

		BlockAction.super.validate(validator);

		action().validate(validator.forChild(".action"));
		offset().validate(validator.forChild(".offset"));

	}

}
