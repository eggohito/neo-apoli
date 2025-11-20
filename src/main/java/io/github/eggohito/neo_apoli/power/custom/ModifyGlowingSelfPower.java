package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public class ModifyGlowingSelfPower extends Power {

	public static final MapCodec<ModifyGlowingSelfPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.optionalFieldOf("use_team_color", new ConstantBooleanProvider(true)).forGetter(ModifyGlowingSelfPower::getUseTeamColors))
		.and(Color.CODEC.optionalFieldOf("color", Argb.DEFAULT).forGetter(ModifyGlowingSelfPower::getColor))
		.apply(instance, ModifyGlowingSelfPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyGlowingSelfPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		BooleanProvider.PACKET_CODEC, ModifyGlowingSelfPower::getUseTeamColors,
		Color.PACKET_CODEC, ModifyGlowingSelfPower::getColor,
		ModifyGlowingSelfPower::new
	);

	private final BooleanProvider useTeamColors;
	private final Color color;

	public ModifyGlowingSelfPower(Optional<Condition> activeCondition, BooleanProvider useTeamColors, Color color) {
		super(activeCondition);
		this.useTeamColors = useTeamColors;
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_GLOWING_SELF_POWER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getUseTeamColors().validate(reporter.makeChild(".use_team_color"));
		getColor().validate(reporter.makeChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyGlowingSelfPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyGlowingSelfPower power) {
			super(holder, power);
		}

		public int getColor(Context context) {
			return this.getPower().getColor().getValue(context.makeChild(".color"));
		}

		public boolean shouldUseTeamColor(Context context) {
			return this.getPower().getUseTeamColors().next(context.makeChild(".use_team_color"));
		}

	}

	public static Context createContext(@Nullable Entity actor, Entity target) {
		return PowerTypes.MODIFY_GLOWING_SELF_POWER.contextBuilder()
			.addNullable(NeoApoliContextParameters.ACTOR, actor)
			.add(NeoApoliContextParameters.TARGET, target)
			.add(NeoApoliContextParameters.THIS_ENTITY, target)
			.add(NeoApoliContextParameters.ENTITY_POS, target.getPos())
			.build(target.getWorld());
	}

}
