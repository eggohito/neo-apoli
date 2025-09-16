package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public class ModifyEntityGlowSelfPower extends Power {

	public static final MapCodec<ModifyEntityGlowSelfPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(ModifyEntityGlowSelfPower::getBiEntityCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("use_team_color", new ConstantBooleanProvider(true)).forGetter(ModifyEntityGlowSelfPower::getUseTeamColors))
		.and(Color.CODEC.optionalFieldOf("color", Argb.DEFAULT).forGetter(ModifyEntityGlowSelfPower::getColor))
		.apply(instance, ModifyEntityGlowSelfPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyEntityGlowSelfPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BiEntityCondition.PACKET_CODEC.encode(buf, power.getBiEntityCondition());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getUseTeamColors());
			Color.PACKET_CODEC.encode(buf, power.getColor());
		},
		(buf, properties, activeCondition) -> new ModifyEntityGlowSelfPower(properties, activeCondition,
			BiEntityCondition.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf),
			Color.PACKET_CODEC.decode(buf)
		)
	);

	private final BiEntityCondition biEntityCondition;
	private final BooleanProvider useTeamColors;
	private final Color color;

	public ModifyEntityGlowSelfPower(Properties properties, Optional<EntityCondition> activeCondition, BiEntityCondition biEntityCondition, BooleanProvider useTeamColors, Color color) {
		super(properties, activeCondition);
		this.biEntityCondition = biEntityCondition;
		this.useTeamColors = useTeamColors;
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ENTITY_GLOW_SELF;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getBiEntityCondition().validate(reporter.makeChild(".bientity_condition"));
		getUseTeamColors().validate(reporter.makeChild(".use_team_color"));
		getColor().validate(reporter.makeChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyEntityGlowSelfPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyEntityGlowSelfPower power) {
			super(holder, power);
		}

		public int getColor(Context context) {
			return this.getPower().getColor().getValue(context.makeChild(".color"));
		}

		public boolean doesApply(Context context) {
			context = this.addPowerContext(context);
			return this.getPower().getBiEntityCondition().test(context.makeChild(".bientity_condition"));
		}

		public boolean shouldUseTeamColor(Context context) {
			context = this.addPowerContext(context);
			return this.getPower().getUseTeamColors().next(context.makeChild(".use_team_color"));
		}

	}

	public static Context createContext(@Nullable Entity actor, Entity target) {
		return PowerTypes.MODIFY_ENTITY_GLOW_SELF.contextBuilder()
			.addNullable(ContextParameters.ACTOR, actor)
			.add(ContextParameters.TARGET, target)
			.add(ContextParameters.ENTITY, target)
			.add(ContextParameters.ENTITY_POS, target.getPos())
			.build(target.getWorld());
	}

}
