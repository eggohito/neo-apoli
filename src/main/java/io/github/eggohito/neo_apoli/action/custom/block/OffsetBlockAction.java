package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record OffsetBlockAction(BlockAction action, Vec3dProvider offset) implements BlockAction {

	public static final MapCodec<OffsetBlockAction> CODEC = MapCodecUtil.lazy(OffsetBlockAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("action").forGetter(OffsetBlockAction::action),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetBlockAction::offset)
	).apply(instance, OffsetBlockAction::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetBlockAction> STREAM_CODEC = StreamCodec.composite(
		BlockAction.STREAM_CODEC, OffsetBlockAction::action,
		Vec3dProvider.STREAM_CODEC, OffsetBlockAction::offset,
		OffsetBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.OFFSET;
	}

	@Override
	public void serverExecute(ServerContext context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Context offsetContext = context.makeChild(".offset");
		Vec3 offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return;
		}

		Level world = context.getWorld();
		BlockPos offsetBlockPos = BlockPos.containing(context.required(NeoApoliContextKeys.BLOCK_POS)
			.getCenter()
			.add(offset));

		if (!world.hasChunkAt(offsetBlockPos)) {
			return;
		}

		Context actionContext = ContextImpl.of(context, builder -> builder
			.add(NeoApoliContextKeys.BLOCK_POS, offsetBlockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, world.getBlockState(offsetBlockPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(offsetBlockPos)));

		action().execute(actionContext.makeChild(".action"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_POS);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		BlockAction.super.validate(reporter);

		action().validate(reporter.forChild(".action"));
		offset().validate(reporter.forChild(".offset"));

	}

}
