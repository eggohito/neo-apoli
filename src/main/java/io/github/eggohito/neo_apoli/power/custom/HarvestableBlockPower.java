package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class HarvestableBlockPower extends Power implements Prioritized<HarvestableBlockPower> {

	public static final MapCodec<HarvestableBlockPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(HarvestableBlockPower::getBlockCondition))
		.and(Codec.BOOL.fieldOf("allow").forGetter(HarvestableBlockPower::isAllowed))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(HarvestableBlockPower::getPriority))
		.apply(instance, HarvestableBlockPower::new));

	public static final PacketCodec<RegistryByteBuf, HarvestableBlockPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, harvestableBlockPower) -> {
			BlockCondition.PACKET_CODEC.encode(buf, harvestableBlockPower.getBlockCondition());
			buf.writeBoolean(harvestableBlockPower.isAllowed());
			buf.writeVarInt(harvestableBlockPower.getPriority());
		},
		(buf, properties, condition) -> new HarvestableBlockPower(properties, condition,
			BlockCondition.PACKET_CODEC.decode(buf),
			buf.readBoolean(),
			buf.readVarInt()
		)
	);

	private final BlockCondition blockCondition;

	private final boolean allow;
	private final int priority;

	public HarvestableBlockPower(Properties properties, EntityCondition activeCondition, BlockCondition blockCondition, boolean allow, int priority) {
		super(properties, activeCondition);
		this.blockCondition = blockCondition;
		this.allow = allow;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.HARVESTABLE_BLOCK;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		getBlockCondition().validate(reporter.makeChild("block_condition"));
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public BlockCondition getBlockCondition() {
		return blockCondition;
	}

	public boolean isAllowed() {
		return allow;
	}

	public static class Impl extends Power.Impl<HarvestableBlockPower> implements Comparable<Impl> {

		protected Impl(@NotNull Entity holder, @NotNull HarvestableBlockPower power) {
			super(holder, power);
		}

		@Override
		public int compareTo(@NotNull HarvestableBlockPower.Impl that) {
			return this.getPower().compareTo(that.getPower());
		}

		public boolean isAllowed() {
			return this.getPower().isAllowed();
		}

		public boolean doesApply(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {

			Context.Builder builder = this.getPowerType().contextBuilder()
				.add(ContextParameters.THIS_ENTITY, holder)
				.add(ContextParameters.BLOCK_STATE, state)
				.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity);

			Context entityContext = builder
				.add(ContextParameters.POSITION, holder.getPos())
				.build(holder.getWorld());
			Context blockContext = builder
				.add(ContextParameters.POSITION, pos.toCenterPos())
				.build(holder.getWorld());

			return this.isActive(entityContext)
				&& this.testAndReport("block_condition", power.getBlockCondition(), blockContext);

		}

	}

	public static boolean canHarvest(PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, BooleanSupplier defaultValue) {
		return PowersComponent.getPowerImpls(player, Impl.class, impl -> impl.doesApply(pos, state, blockEntity))
			.stream()
			.max(Impl::compareTo)
			.map(Impl::isAllowed)
			.orElseGet(defaultValue::getAsBoolean);
	}

}
