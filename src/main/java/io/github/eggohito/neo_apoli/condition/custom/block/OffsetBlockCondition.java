package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record OffsetBlockCondition(BlockCondition condition, Vec3Provider offset) implements BlockCondition {

	private static final ContextKeySet CONDITION_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	public static final MapCodec<OffsetBlockCondition> MAP_CODEC = MapCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("condition").forGetter(OffsetBlockCondition::condition),
		Vec3Provider.CODEC.fieldOf("offset").forGetter(OffsetBlockCondition::offset)
	).apply(instance, OffsetBlockCondition::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> StreamCodec.composite(
		BlockCondition.STREAM_CODEC, OffsetBlockCondition::condition,
		Vec3Provider.STREAM_CODEC, OffsetBlockCondition::offset,
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

		Context offsetContext = context.forChild(".offset");
		Vec3 offset = offset().nextVec3(offsetContext);

		if (offsetContext.hasErrors()) {
			return false;
		}

		Level level = context.level();
		BlockPos offsetBlockPos = BlockPos.containing(context.getRequired(NeoApoliContextParams.BLOCK_POS)
			.getCenter()
			.add(offset));

		if (!level.hasChunkAt(offsetBlockPos)) {
			return false;
		}

		Context conditionContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.BLOCK_POS, offsetBlockPos)
			.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(offsetBlockPos))
			.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(offsetBlockPos))
			.build(level);

		return condition().test(conditionContext.forChild(".condition"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_POS);
	}

	@Override
	public void validate(Context.Validator validator) {

		BlockCondition.super.validate(validator);

		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMS).forChild(".condition"));
		offset().validate(validator.forChild(".offset"));

	}

}
