package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@Getter
public class ModifyBlockHarvestablePower extends Power implements Prioritized<ModifyBlockHarvestablePower> {

	public static final MapCodec<ModifyBlockHarvestablePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyBlockHarvestablePower::getAllow))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockHarvestablePower::getPriority))
		.apply(instance, ModifyBlockHarvestablePower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyBlockHarvestablePower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		BooleanProvider.PACKET_CODEC, ModifyBlockHarvestablePower::getAllow,
		PacketCodecs.INTEGER, ModifyBlockHarvestablePower::getPriority,
		ModifyBlockHarvestablePower::new
	);

	private final BooleanProvider allow;
	private final int priority;

	public ModifyBlockHarvestablePower(Optional<Condition> activeCondition, BooleanProvider allow, int priority) {
		super(activeCondition);
		this.allow = allow;
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
		getAllow().validate(reporter.makeChild(".allow"));
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
			return power.getAllow().next(context.makeChild(".allow"));
		}

	}

	public static boolean modify(Context context, InstanceCollection<Instance> instances, BooleanSupplier defaultValue) {

		for (var instance : instances) {

			try {

				ErrorReporter reporter = instance.createReporter();
				Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {
					return instance.isAllowed(instanceContext);
				}

			}

			finally {
				context.markInActive(instance);
			}

		}

		return defaultValue.getAsBoolean();

	}

	public static Context createContext(PlayerEntity player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_HARVESTABLE.contextBuilder()
			.add(NeoApoliContextParameters.BLOCK_POS, blockPos)
			.add(NeoApoliContextParameters.BLOCK_STATE, blockState)
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, blockEntity)
			.add(NeoApoliContextParameters.THIS_ENTITY, player)
			.add(NeoApoliContextParameters.ENTITY_POS, player.getPos())
			.build(player.getWorld());
	}

}
