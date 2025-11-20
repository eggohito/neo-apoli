package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public record OffsetBlockCondition(BlockCondition condition, Vec3dProvider offset) implements BlockCondition {

	public static final MapCodec<OffsetBlockCondition> CODEC = MapCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("condition").forGetter(OffsetBlockCondition::condition),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetBlockCondition::offset)
	).apply(instance, OffsetBlockCondition::new)));

	public static final PacketCodec<RegistryByteBuf, OffsetBlockCondition> PACKET_CODEC = PacketCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, OffsetBlockCondition::condition,
		Vec3dProvider.PACKET_CODEC, OffsetBlockCondition::offset,
		OffsetBlockCondition::new
	));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.OFFSET;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Context offsetContext = context.makeChild(".offset");
		Vec3d offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return false;
		}

		World world = context.getWorld();
		BlockPos offsetBlockPos = BlockPos.ofFloored(context.required(NeoApoliContextParameters.BLOCK_POS)
			.toCenterPos()
			.add(offset));

		if (!world.isChunkLoaded(offsetBlockPos)) {
			return false;
		}

		Context conditionContext = ContextImpl.of(context, builder -> builder
			.add(NeoApoliContextParameters.BLOCK_POS, offsetBlockPos)
			.add(NeoApoliContextParameters.BLOCK_STATE, world.getBlockState(offsetBlockPos))
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(offsetBlockPos)));

		return condition().test(conditionContext.makeChild(".condition"));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.BLOCK_POS);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		BlockCondition.super.validate(reporter);

		condition().validate(reporter.makeChild(".condition"));
		offset().validate(reporter.makeChild(".offset"));

	}

}
