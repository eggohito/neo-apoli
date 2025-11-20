package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public record OffsetBlockAction(BlockAction action, Vec3dProvider offset) implements BlockAction {

	public static final MapCodec<OffsetBlockAction> CODEC = MapCodecUtil.lazy(OffsetBlockAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("action").forGetter(OffsetBlockAction::action),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetBlockAction::offset)
	).apply(instance, OffsetBlockAction::new)));

	public static final PacketCodec<RegistryByteBuf, OffsetBlockAction> PACKET_CODEC = PacketCodec.tuple(
		BlockAction.PACKET_CODEC, OffsetBlockAction::action,
		Vec3dProvider.PACKET_CODEC, OffsetBlockAction::offset,
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
		Vec3d offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return;
		}

		World world = context.getWorld();
		BlockPos offsetBlockPos = BlockPos.ofFloored(context.required(NeoApoliContextParameters.BLOCK_POS)
			.toCenterPos()
			.add(offset));

		if (!world.isChunkLoaded(offsetBlockPos)) {
			return;
		}

		Context actionContext = ContextImpl.of(context, builder -> builder
			.add(NeoApoliContextParameters.BLOCK_POS, offsetBlockPos)
			.add(NeoApoliContextParameters.BLOCK_STATE, world.getBlockState(offsetBlockPos))
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(offsetBlockPos)));

		action().execute(actionContext.makeChild(".action"));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.BLOCK_POS);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		BlockAction.super.validate(reporter);

		action().validate(reporter.makeChild(".action"));
		offset().validate(reporter.makeChild(".offset"));

	}

}
