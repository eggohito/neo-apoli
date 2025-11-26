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
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyBlockHarvestablePower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		BooleanProvider.STREAM_CODEC, ModifyBlockHarvestablePower::getAllow,
		ByteBufCodecs.INT, ModifyBlockHarvestablePower::getPriority,
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
		return new io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestablePower.Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		super.validate(reporter);
		getAllow().validate(reporter.forChild(".allow"));
	}

	public static class Instance extends Power.Instance<ModifyBlockHarvestablePower> implements Comparable<io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestablePower.Instance> {

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

	public static boolean modify(Context context, BooleanSupplier defaultValue) {

		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		InstanceCollection<io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestablePower.Instance> instances = new InstanceCollection<>(holder, io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestablePower.Instance.class);

		return modify(context, instances, defaultValue);

	}

	public static boolean modify(Context context, InstanceCollection<io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestablePower.Instance> instances, BooleanSupplier defaultValue) {

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {
					return instance.isAllowed(instanceContext);
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return defaultValue.getAsBoolean();

	}

	public static Context createContext(Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_HARVESTABLE.contextBuilder()
			.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, blockState)
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, blockEntity)
			.add(NeoApoliContextKeys.THIS_ENTITY, player)
			.add(NeoApoliContextKeys.ENTITY_POS, player.position())
			.build(player.level());
	}

}
