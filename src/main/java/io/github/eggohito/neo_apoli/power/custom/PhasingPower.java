package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.IsSneakingEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class PhasingPower extends Power {

	public static final MapCodec<PhasingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BlockCondition.CODEC.fieldOf("block_condition").forGetter(PhasingPower::getBlockCondition))
		.and(EntityCondition.CODEC.optionalFieldOf("phase_down_condition", new IsSneakingEntityCondition()).forGetter(PhasingPower::getPhaseDownCondition))
		.and(RenderType.CODEC.optionalFieldOf("render_type", RenderType.BLINDNESS).forGetter(PhasingPower::getRenderType))
		.and(Codec.floatRange(2.0F, Float.MAX_VALUE).optionalFieldOf("view_distance", 8.0F).forGetter(PhasingPower::getViewDistance))
		.apply(instance, PhasingPower::new));

	public static final PacketCodec<RegistryByteBuf, PhasingPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BlockCondition.PACKET_CODEC.encode(buf, power.getBlockCondition());
			EntityCondition.PACKET_CODEC.encode(buf, power.getPhaseDownCondition());
			RenderType.PACKET_CODEC.encode(buf, power.getRenderType());
			buf.writeFloat(power.getViewDistance());
		},
		(buf, properties, activeCondition) -> new PhasingPower(properties, activeCondition,
			BlockCondition.PACKET_CODEC.decode(buf),
			EntityCondition.PACKET_CODEC.decode(buf),
			RenderType.PACKET_CODEC.decode(buf),
			buf.readFloat()
		)
	);

	private final BlockCondition blockCondition;
	private final EntityCondition phaseDownCondition;

	private final RenderType renderType;
	private final float viewDistance;

	public PhasingPower(Properties properties, Optional<EntityCondition> activeCondition, BlockCondition blockCondition, EntityCondition phaseDownCondition, RenderType renderType, float viewDistance) {
		super(properties, activeCondition);
		this.blockCondition = blockCondition;
		this.phaseDownCondition = phaseDownCondition;
		this.renderType = renderType;
		this.viewDistance = viewDistance;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.PHASING;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<PhasingPower> {

		protected Instance(@NotNull Entity holder, @NotNull PhasingPower power) {
			super(holder, power);
		}

		public RenderType getRenderType() {
			return this.getPower().getRenderType();
		}

		public float getViewDistance() {
			return this.getPower().getViewDistance();
		}

		public boolean doesApply(Context context) {
			context = this.addPowerContext(context);
			return this.isActive(context)
				&& power.getBlockCondition().test(context.makeChild(".block_condition"));
		}

		public boolean shouldPhaseDown(Context context, VoxelShape shape) {

			BlockPos blockPos = context.required(ContextParameters.BLOCK_POS);
			context = this.addPowerContext(context);

			return holder.getY() < (double) blockPos.getY() + shape.getMax(Direction.Axis.Y) - (holder.isOnGround() ? 8.05 / 16.0 : 0.0015)
				|| power.getPhaseDownCondition().test(context.makeChild(".phase_down_condition"));

		}

	}

	public enum RenderType implements StringIdentifiable {

		BLINDNESS("blindness"),
		NONE("none");

		public static final Codec<RenderType> CODEC = CodecUtil.enumType(RenderType.class);
		public static final PacketCodec<ByteBuf, RenderType> PACKET_CODEC = PacketCodecUtil.enumType(RenderType.class);

		private final String name;

		RenderType(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}

	}

	public static Context createContext(Entity entity, SavedBlockPosition savedBlock) {
		return PowerTypes.PHASING.contextBuilder()
			.add(ContextParameters.BLOCK_POS, savedBlock.getBlockPos())
			.add(ContextParameters.BLOCK_STATE, savedBlock.getBlockState())
			.addNullable(ContextParameters.BLOCK_ENTITY, savedBlock.getBlockEntity())
			.add(ContextParameters.ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, entity.getPos())
			.build(entity.getWorld());
	}

}
