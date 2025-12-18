package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record OffsetBlockCondition(BlockCondition condition, Vec3Provider offset) implements BlockCondition {

	public static final MapCodec<OffsetBlockCondition> CODEC = MapCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
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
		Vec3 offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return false;
		}

		Level level = context.getLevel();
		BlockPos offsetBlockPos = BlockPos.containing(context.required(NeoApoliContextKeys.BLOCK_POS)
			.getCenter()
			.add(offset));

		if (!level.hasChunkAt(offsetBlockPos)) {
			return false;
		}

		Context conditionContext = new Context.Builder(context)
			.add(NeoApoliContextKeys.BLOCK_POS, offsetBlockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, level.getBlockState(offsetBlockPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, level.getBlockEntity(offsetBlockPos))
			.build(level);

		return condition().test(conditionContext.forChild(".condition"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_POS);
	}

	@Override
	public void validate(Context.Validator validator) {

		BlockCondition.super.validate(validator);

		condition().validate(validator.forChild(".condition"));
		offset().validate(validator.forChild(".offset"));

	}

}
