package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

@EqualsAndHashCode
@Data
public final class OffsetEntityCondition extends EntityCondition {

	public static final MapCodec<OffsetEntityCondition> CODEC = MapCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.fieldOf("condition").forGetter(OffsetEntityCondition::condition),
		Vec3d.CODEC.fieldOf("offset").forGetter(OffsetEntityCondition::offset)
	).apply(instance, OffsetEntityCondition::new)));
	public static final PacketCodec<RegistryByteBuf, OffsetEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> PacketCodec.tuple(
		EntityCondition.PACKET_CODEC, OffsetEntityCondition::condition,
		Vec3d.PACKET_CODEC, OffsetEntityCondition::offset,
		OffsetEntityCondition::new
	));

	private final EntityCondition condition;
	private final Vec3d offset;

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.OFFSET;
	}

	@Override
	protected boolean impl(Context context) {

		Vec3d offsetPos = context.required(ContextParameters.ENTITY_POS).add(offset());
		context = new ContextImpl.Builder(context)
			.add(ContextParameters.ENTITY_POS, offsetPos)
			.build(context.getWorld());

		return condition().test(context.makeChild(".condition"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		condition().validate(reporter.makeChild(".condition"));
	}

}
