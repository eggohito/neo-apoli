package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record OffsetEntityCondition(EntityCondition condition, Vec3Provider offset) implements EntityCondition {

	public static final MapCodec<OffsetEntityCondition> CODEC = MapCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.fieldOf("condition").forGetter(OffsetEntityCondition::condition),
		Vec3Provider.CODEC.fieldOf("offset").forGetter(OffsetEntityCondition::offset)
	).apply(instance, OffsetEntityCondition::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, OffsetEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> StreamCodec.composite(
		EntityCondition.STREAM_CODEC, OffsetEntityCondition::condition,
		Vec3Provider.STREAM_CODEC, OffsetEntityCondition::offset,
		OffsetEntityCondition::new
	));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.OFFSET;
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

		Vec3 offsetPos = context.required(NeoApoliContextKeys.THIS_POS).add(offset);
		Context conditionContext = new Context.Builder(context)
			.add(NeoApoliContextKeys.THIS_POS, offsetPos)
			.build(context.getLevel());

		return condition().test(conditionContext.forChild(".condition"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.THIS_POS);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		EntityCondition.super.validate(reporter);

		condition().validate(reporter.forChild(".condition"));
		offset().validate(reporter.forChild(".offset"));

	}

}
