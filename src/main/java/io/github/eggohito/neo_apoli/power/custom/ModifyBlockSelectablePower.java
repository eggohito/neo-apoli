package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

@Getter
public class ModifyBlockSelectablePower extends Power implements Prioritized<ModifyBlockSelectablePower> {

	public static final MapCodec<ModifyBlockSelectablePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(ModifyBlockSelectablePower::getBlockCondition))
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyBlockSelectablePower::getAllow))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockSelectablePower::getPriority))
		.apply(instance, ModifyBlockSelectablePower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyBlockSelectablePower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BlockCondition.PACKET_CODEC.encode(buf, power.getBlockCondition());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getAllow());
			buf.writeVarInt(power.getPriority());
		},
		(buf, properties, activeCondition) -> new ModifyBlockSelectablePower(properties, activeCondition,
			BlockCondition.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf),
			buf.readVarInt()
		)
	);

	private final BlockCondition blockCondition;
	private final BooleanProvider allow;
	private final int priority;

	public ModifyBlockSelectablePower(Properties properties, Optional<EntityCondition> activeCondition, BlockCondition blockCondition, BooleanProvider allow, int priority) {
		super(properties, activeCondition);
		this.blockCondition = blockCondition;
		this.allow = allow;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_BLOCK_SELECTABLE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getBlockCondition().validate(reporter.makeChild(".block_condition"));
		getAllow().validate(reporter.makeChild(".allow"));

	}

	public static class Instance extends Power.Instance<ModifyBlockSelectablePower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyBlockSelectablePower power) {
			super(holder, power);
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

	public static VoxelShape modifyOrElseGet(Context context, Supplier<@NotNull VoxelShape> defaultValue) {

		InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(context.nullable(ContextParameters.ENTITY), Instance.class, instance -> instance.doesApply(context));
		Iterator<Instance> iterator = instanceCollection.iterator();

		if (!iterator.hasNext() || iterator.next().isAllowed(context)) {
			return defaultValue.get();
		}

		else {
			return VoxelShapes.empty();
		}

	}

	public static Context createContext(@NotNull Entity entity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_SELECTABLE.contextBuilder()
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, blockState)
			.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
			.add(ContextParameters.ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, entity.getPos())
			.build(entity.getWorld());
	}

}
