package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@EqualsAndHashCode
@Data
public final class OffsetBlockCondition extends BlockCondition {

	public static final MapCodec<OffsetBlockCondition> CODEC = MapCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("condition").forGetter(OffsetBlockCondition::condition),
		Vec3d.CODEC.fieldOf("offset").forGetter(OffsetBlockCondition::offset)
	).apply(instance, OffsetBlockCondition::new)));
	public static final PacketCodec<RegistryByteBuf, OffsetBlockCondition> PACKET_CODEC = PacketCodecUtil.lazy(OffsetBlockCondition.class.getSimpleName(), () -> PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, OffsetBlockCondition::condition,
		Vec3d.PACKET_CODEC, OffsetBlockCondition::offset,
		OffsetBlockCondition::new
	));

	private final BlockCondition condition;
	private final Vec3d offset;

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.OFFSET;
	}

	@Override
	protected boolean impl(Context context) {

		Vec3d offsetPos = context.required(ContextParameters.BLOCK_POS).toCenterPos().add(offset());
		context = new ContextImpl.Builder(context)
			.add(ContextParameters.BLOCK_POS, BlockPos.ofFloored(offsetPos))
			.build(context.getWorld());

		return condition().test(context.makeChild(".condition"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		condition().validate(reporter.makeChild(".condition"));
	}

}
