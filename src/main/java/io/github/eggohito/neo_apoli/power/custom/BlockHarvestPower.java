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
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BooleanSupplier;

@Getter
public class BlockHarvestPower extends Power implements Prioritized<BlockHarvestPower> {

	public static final MapCodec<BlockHarvestPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(BlockHarvestPower::getBlockCondition))
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(BlockHarvestPower::getAllowedProvider))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(BlockHarvestPower::getPriority))
		.apply(instance, BlockHarvestPower::new));

	public static final PacketCodec<RegistryByteBuf, BlockHarvestPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BlockCondition.PACKET_CODEC.encode(buf, power.getBlockCondition());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getAllowedProvider());
			buf.writeVarInt(power.getPriority());
		},
		(buf, properties, condition) -> new BlockHarvestPower(properties, condition,
			BlockCondition.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf),
			buf.readVarInt()
		)
	);

	private final BlockCondition blockCondition;

	private final BooleanProvider allowedProvider;
	private final int priority;

	public BlockHarvestPower(Properties properties, EntityCondition activeCondition, BlockCondition blockCondition, BooleanProvider allowedProvider, int priority) {
		super(properties, activeCondition);
		this.blockCondition = blockCondition;
		this.allowedProvider = allowedProvider;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.BLOCK_HARVEST;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getBlockCondition().validate(reporter.makeChild(".block_condition"));
		getAllowedProvider().validate(reporter.makeChild(".allow"));

	}

	public static class Impl extends Power.Impl<BlockHarvestPower> implements Prioritized<Impl> {

		protected Impl(@NotNull Entity holder, @NotNull BlockHarvestPower power) {
			super(holder, power);
		}

		@Override
		public int getPriority() {
			return this.getPower().getPriority();
		}

		public boolean isAllowed(Context context) {
			context = this.copyWithPowerContext(context);
			return this.getPower().getAllowedProvider().next(context.makeChild(".allow"));
		}

		public boolean doesApply(Context context) {
			context = this.copyWithPowerContext(context);
			return this.isActive(context)
				&& power.getBlockCondition().test(context.makeChild(".block_condition"));
		}

	}

	public static boolean canHarvest(Context context, BooleanSupplier defaultValue) {

		List<Impl> impls = PowersComponent.getPowerImpls(context.required(ContextParameters.ENTITY), Impl.class, impl -> impl.doesApply(context));
		impls.sort(Impl::compareTo);

		if (impls.isEmpty()) {
			return defaultValue.getAsBoolean();
		}

		else {
			return impls.getLast().isAllowed(context);
		}

	}

}
