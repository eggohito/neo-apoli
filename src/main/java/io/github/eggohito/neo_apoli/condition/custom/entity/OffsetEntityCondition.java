package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public record OffsetEntityCondition(EntityCondition condition, Vec3dProvider offset) implements EntityCondition {

	public static final MapCodec<OffsetEntityCondition> CODEC = MapCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.fieldOf("condition").forGetter(OffsetEntityCondition::condition),
		Vec3dProvider.CODEC.fieldOf("offset").forGetter(OffsetEntityCondition::offset)
	).apply(instance, OffsetEntityCondition::new)));

	public static final PacketCodec<RegistryByteBuf, OffsetEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> PacketCodec.tuple(
		EntityCondition.PACKET_CODEC, OffsetEntityCondition::condition,
		Vec3dProvider.PACKET_CODEC, OffsetEntityCondition::offset,
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

		Context offsetContext = context.makeChild(".offset");
		Vec3d offset = offset().next(offsetContext);

		if (offsetContext.hasErrors()) {
			return false;
		}

		Vec3d offsetPos = context.required(NeoApoliContextParameters.ENTITY_POS).add(offset);
		Context conditionContext = ContextImpl.of(context, builder -> builder.add(NeoApoliContextParameters.ENTITY_POS, offsetPos));

		return condition().test(conditionContext.makeChild(".condition"));

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.ENTITY_POS);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		EntityCondition.super.validate(reporter);

		condition().validate(reporter.makeChild(".condition"));
		offset().validate(reporter.makeChild(".offset"));

	}

}
