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
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class HarvestableBlockPower extends Power implements Prioritized {

	public static final MapCodec<HarvestableBlockPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonAndConditionFields(instance)
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

	public static final ContextType CONTEXT_TYPE = createContextType(builder -> builder
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY));

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
	public Impl createImpl(Entity holder) {
		return new Impl(holder);
	}

	@Override
	public ContextType getContextType() {
		return CONTEXT_TYPE;
	}

	@Override
	public void validate(ErrorReporter reporter) {
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

	public class Impl extends Power.Impl<HarvestableBlockPower> implements Comparable<Impl> {

		protected Impl(@NotNull Entity holder) {
			super(holder, HarvestableBlockPower.this);
		}

		@Override
		public int compareTo(@NotNull HarvestableBlockPower.Impl that) {
			return Integer.compare(this.getPriority(), that.getPriority());
		}

		public int getPriority() {
			return this.getPower().getPriority();
		}

		public boolean isAllowed() {
			return this.getPower().isAllowed();
		}

		public boolean doesApply(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {

			Context context = this.createContext(builder -> builder
				.add(ContextParameters.BLOCK_STATE, state)
				.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity));

			return this.isActive(context)
				&& this.testAndReport("block_condition", getBlockCondition(), context.copy(builder -> builder.add(ContextParameters.POSITION, pos.toCenterPos())));

		}

	}

	public static boolean canHarvest(PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, BooleanSupplier defaultValue) {
		return PowersComponent.getPowers(player, Impl.class, impl -> impl.doesApply(pos, state, blockEntity))
			.stream()
			.max(Impl::compareTo)
			.map(Impl::isAllowed)
			.orElseGet(defaultValue::getAsBoolean);
	}

}
