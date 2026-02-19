package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record OffsetEntityCondition(EntityCondition condition, Vec3Provider offset) implements EntityCondition {

	private static final ContextKeySet CONDITION_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.THIS_POS)
		.build();

	public static final MapCodec<OffsetEntityCondition> MAP_CODEC = MapCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
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
		Vec3 offset = offset().nextVec3(offsetContext);

		if (offsetContext.hasErrors()) {
			return false;
		}

		Vec3 offsetPos = context.getRequired(NeoApoliContextParams.THIS_POS).add(offset);
		Context conditionContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.THIS_POS, offsetPos)
			.build(context.level());

		return condition().test(conditionContext.forChild(".condition"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_POS);
	}

	@Override
	public void validate(Context.Validator validator) {

		EntityCondition.super.validate(validator);

		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMS).forChild(".condition"));
		offset().validate(validator.forChild(".offset"));

	}

}
