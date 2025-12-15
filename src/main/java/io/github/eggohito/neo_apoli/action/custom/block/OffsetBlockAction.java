package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record OffsetBlockAction(BlockAction action, Vec3Provider offset) implements BlockAction {

	public static final MapCodec<OffsetBlockAction> CODEC = MapCodecUtil.lazy(OffsetBlockAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
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

		if (!(context.getLevel() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Context offsetContext = context.forChild(".offset");
		Vec3 offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return;
		}

		BlockPos offsetBlockPos = BlockPos.containing(context.required(NeoApoliContextKeys.BLOCK_POS)
			.getCenter()
			.add(offset));

		if (!serverLevel.hasChunkAt(offsetBlockPos)) {
			return;
		}

		Context actionContext = new Context.Builder(context)
			.add(NeoApoliContextKeys.BLOCK_POS, offsetBlockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, serverLevel.getBlockState(offsetBlockPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, serverLevel.getBlockEntity(offsetBlockPos))
			.build(serverLevel);

		action().execute(actionContext.forChild(".action"));

	}

	@Override
	public void validate(ProblemReporter reporter) {

		BlockAction.super.validate(reporter);

		action().validate(reporter.forChild(".action"));
		offset().validate(reporter.forChild(".offset"));

	}

}
