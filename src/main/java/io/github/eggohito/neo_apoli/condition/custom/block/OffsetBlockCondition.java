package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record OffsetBlockCondition(BlockCondition condition, Vec3dProvider offset) implements BlockCondition {

	public static final MapCodec<OffsetBlockCondition> CODEC = MapCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("condition").forGetter(OffsetBlockCondition::condition),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetBlockCondition::offset)
	).apply(instance, OffsetBlockCondition::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> StreamCodec.composite(
		BlockCondition.STREAM_CODEC, OffsetBlockCondition::condition,
		Vec3dProvider.STREAM_CODEC, OffsetBlockCondition::offset,
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
		Vec3 offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return false;
		}

		Level world = context.getWorld();
		BlockPos offsetBlockPos = BlockPos.containing(context.required(NeoApoliContextKeys.BLOCK_POS)
			.getCenter()
			.add(offset));

		if (!world.hasChunkAt(offsetBlockPos)) {
			return false;
		}

		Context conditionContext = ContextImpl.of(context, builder -> builder
			.add(NeoApoliContextKeys.BLOCK_POS, offsetBlockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, world.getBlockState(offsetBlockPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(offsetBlockPos)));

		return condition().test(conditionContext.makeChild(".condition"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_POS);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		BlockCondition.super.validate(reporter);

		condition().validate(reporter.forChild(".condition"));
		offset().validate(reporter.forChild(".offset"));

	}

}
