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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Getter
public class ModifyBlockSelectabilityPower extends Power implements Prioritized<ModifyBlockSelectabilityPower> {

	public static final MapCodec<ModifyBlockSelectabilityPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(ModifyBlockSelectabilityPower::getBlockCondition))
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyBlockSelectabilityPower::getAllow))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockSelectabilityPower::getPriority))
		.apply(instance, ModifyBlockSelectabilityPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyBlockSelectabilityPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BlockCondition.PACKET_CODEC.encode(buf, power.getBlockCondition());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getAllow());
			buf.writeVarInt(power.getPriority());
		},
		(buf, properties, activeCondition) -> new ModifyBlockSelectabilityPower(properties, activeCondition,
			BlockCondition.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf),
			buf.readVarInt()
		)
	);

	private final BlockCondition blockCondition;
	private final BooleanProvider allow;
	private final int priority;

	public ModifyBlockSelectabilityPower(Properties properties, Optional<EntityCondition> activeCondition, BlockCondition blockCondition, BooleanProvider allow, int priority) {
		super(properties, activeCondition);
		this.blockCondition = blockCondition;
		this.allow = allow;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_BLOCK_SELECTABILITY;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getBlockCondition().validate(reporter.makeChild(".block_condition"));
		getAllow().validate(reporter.makeChild(".allow"));

	}

	public static class Impl extends Power.Impl<ModifyBlockSelectabilityPower> implements Prioritized<Impl> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyBlockSelectabilityPower power) {
			super(holder, power);
		}

		@Override
		public int getPriority() {
			return power.getPriority();
		}

		public boolean doesApply(Context context) {
			context = this.addPowerContext(context);
			return this.isActive(context)
				&& power.getBlockCondition().test(context.makeChild(".block_condition"));
		}

		public boolean isAllowed(Context context) {
			return power.getAllow().next(this.addPowerContext(context).makeChild(".allow"));
		}

	}

	public static VoxelShape modifySelectingOutlineShape(Context context, Supplier<@NotNull VoxelShape> defaultValue) {

		List<Impl> impls = PowersComponent.getPowerImpls(context.nullable(ContextParameters.ENTITY), Impl.class, impl -> impl.doesApply(context));
		impls.sort(Impl::compareTo);

		if (impls.isEmpty()) {
			return defaultValue.get();
		}

		else if (!impls.getLast().isAllowed(context)) {
			return VoxelShapes.empty();
		}

		else {
			return defaultValue.get();
		}

	}

	public static Context createContext(@NotNull Entity entity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_SELECTABILITY.contextBuilder()
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, blockState)
			.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
			.add(ContextParameters.ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, entity.getPos())
			.build(entity.getWorld());
	}

}
