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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;

@Getter
public class ModifyBlockHarvestablePower extends Power implements Prioritized<ModifyBlockHarvestablePower> {

	public static final MapCodec<ModifyBlockHarvestablePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(ModifyBlockHarvestablePower::getBlockCondition))
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyBlockHarvestablePower::getAllowedProvider))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockHarvestablePower::getPriority))
		.apply(instance, ModifyBlockHarvestablePower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyBlockHarvestablePower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BlockCondition.PACKET_CODEC.encode(buf, power.getBlockCondition());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getAllowedProvider());
			buf.writeVarInt(power.getPriority());
		},
		(buf, properties, condition) -> new ModifyBlockHarvestablePower(properties, condition,
			BlockCondition.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf),
			buf.readVarInt()
		)
	);

	private final BlockCondition blockCondition;

	private final BooleanProvider allowedProvider;
	private final int priority;

	public ModifyBlockHarvestablePower(Properties properties, Optional<EntityCondition> activeCondition, BlockCondition blockCondition, BooleanProvider allowedProvider, int priority) {
		super(properties, activeCondition);
		this.blockCondition = blockCondition;
		this.allowedProvider = allowedProvider;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_BLOCK_HARVESTABLE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getBlockCondition().validate(reporter.makeChild(".block_condition"));
		getAllowedProvider().validate(reporter.makeChild(".allow"));

	}

	public static class Instance extends Power.Instance<ModifyBlockHarvestablePower> implements Comparable<Instance> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyBlockHarvestablePower power) {
			super(holder, power);
		}

		@Override
		public int compareTo(@NotNull ModifyBlockHarvestablePower.Instance that) {
			return this.getPower().compareTo(that.getPower());
		}

		public boolean isAllowed(Context context) {
			context = this.addPowerContext(context);
			return this.getPower().getAllowedProvider().next(context.makeChild(".allow"));
		}

		public boolean doesApply(Context context) {
			context = this.addPowerContext(context);
			return this.isActive(context)
				&& power.getBlockCondition().test(context.makeChild(".block_condition"));
		}

	}

	public static boolean canHarvest(Context context, BooleanSupplier defaultValue) {

		InstanceCollection<Instance> instanceCollection = new InstanceCollection<>(context.required(ContextParameters.ENTITY), Instance.class, instance -> instance.doesApply(context));
		Iterator<Instance> iterator = instanceCollection.iterator();

		if (iterator.hasNext()) {
			return iterator.next().isAllowed(context);
		}

		else {
			return defaultValue.getAsBoolean();
		}

	}

	public static Context createContext(PlayerEntity player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_HARVESTABLE.contextBuilder()
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, blockState)
			.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
			.add(ContextParameters.ENTITY, player)
			.add(ContextParameters.ENTITY_POS, player.getPos())
			.build(player.getWorld());
	}

}
