package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.block.NothingBlockAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class OnBlockBreakPower extends Power {

	public static final MapCodec<OnBlockBreakPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonAndConditionFields(instance)
		.and(BlockAction.CODEC.optionalFieldOf("block_action", new NothingBlockAction()).forGetter(OnBlockBreakPower::getBlockAction))
		.and(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(OnBlockBreakPower::getBlockCondition))
		.and(EntityAction.CODEC.optionalFieldOf("entity_action", new NothingEntityAction()).forGetter(OnBlockBreakPower::getEntityAction))
		.and(Direction.CODEC.listOf().optionalFieldOf("directions", new ObjectArrayList<>()).forGetter(OnBlockBreakPower::getDirections))
		.and(Codec.BOOL.optionalFieldOf("only_when_harvested", false).forGetter(OnBlockBreakPower::onlyWhenHarvested))
		.apply(instance, OnBlockBreakPower::new));

	public static final PacketCodec<RegistryByteBuf, OnBlockBreakPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, onBlockBreakPower) -> {
			BlockAction.PACKET_CODEC.encode(buf, onBlockBreakPower.getBlockAction());
			BlockCondition.PACKET_CODEC.encode(buf, onBlockBreakPower.getBlockCondition());
			EntityAction.PACKET_CODEC.encode(buf, onBlockBreakPower.getEntityAction());
			NeoApoliPacketCodecs.DIRECTIONS.encode(buf, onBlockBreakPower.getDirections());
			buf.writeBoolean(onBlockBreakPower.onlyWhenHarvested());
		},
		(buf, properties, condition) -> new OnBlockBreakPower(properties, condition,
			BlockAction.PACKET_CODEC.decode(buf),
			BlockCondition.PACKET_CODEC.decode(buf),
			EntityAction.PACKET_CODEC.decode(buf),
			NeoApoliPacketCodecs.DIRECTIONS.decode(buf),
			buf.readBoolean()
		)
	);

	public static final ContextType CONTEXT_TYPE = createContextType(builder -> builder
		.allow(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.BLOCK_ENTITY)
		.allow(ContextParameters.DIRECTION));

	private final BlockAction blockAction;
	private final BlockCondition blockCondition;

	private final EntityAction entityAction;
	private final List<Direction> directions;

	private final boolean onlyWhenHarvested;

	public OnBlockBreakPower(Properties properties, EntityCondition activeCondition, BlockAction blockAction, BlockCondition blockCondition, EntityAction entityAction, List<Direction> directions, boolean onlyWhenHarvested) {
		super(properties, activeCondition);
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
		this.entityAction = entityAction;
		this.directions = directions;
		this.onlyWhenHarvested = onlyWhenHarvested;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.ON_BLOCK_BREAK;
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
		getBlockAction().validate(reporter.makeChild("block_action"));
		getBlockCondition().validate(reporter.makeChild("block_condition"));
		getEntityAction().validate(reporter.makeChild("entity_action"));
	}

	public BlockAction getBlockAction() {
		return blockAction;
	}

	public BlockCondition getBlockCondition() {
		return blockCondition;
	}

	public EntityAction getEntityAction() {
		return entityAction;
	}

	public List<Direction> getDirections() {
		return directions;
	}

	public boolean onlyWhenHarvested() {
		return onlyWhenHarvested;
	}

	public class Impl extends Power.Impl<OnBlockBreakPower> {

		protected Impl(@NotNull Entity holder) {
			super(holder, OnBlockBreakPower.this);
		}

		public void execute(Context entityContext, Context blockContext) {
			executeAndReport("block_action", getBlockAction(), blockContext);
			executeAndReport("entity_action", getEntityAction(), entityContext);
		}

		public boolean doesApply(Context entityContext, Context blockContext, boolean harvested) {
			return (!onlyWhenHarvested() || harvested)
				&& (directions.isEmpty() || blockContext.optionalParameter(ContextParameters.DIRECTION).map(directions::contains).orElse(true))
				&& this.isActive(entityContext)
				&& this.testAndReport("block_condition", getBlockCondition(), blockContext);
		}

	}

	public static void execute(PlayerEntity player, World world, BlockPos pos, BlockState blockState, BlockEntity blockEntity, Direction direction, boolean harvested) {

		Set<Power.Impl<?>> impls = NeoApoliEntityComponents.POWERS.get(player).getPowers(true);
		for (var impl : impls) {

			if (!(impl instanceof OnBlockBreakPower.Impl blockBreakPower)) {
				continue;
			}

			Context.Builder builder = blockBreakPower.createContextBuilder()
				.addNullable(ContextParameters.BLOCK_STATE, blockState)
				.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
				.addNullable(ContextParameters.DIRECTION, direction);

			Context entityContext = builder.build(world);
			Context blockContext = builder.add(ContextParameters.POSITION, pos.toCenterPos()).build(world);

			if (blockBreakPower.doesApply(entityContext, blockContext, harvested)) {
				blockBreakPower.execute(entityContext, blockContext);
			}

		}

	}

}
