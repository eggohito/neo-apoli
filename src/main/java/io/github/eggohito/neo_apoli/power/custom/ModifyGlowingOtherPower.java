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
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class ModifyGlowingOtherPower extends Power {

	public static final MapCodec<ModifyGlowingOtherPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.optionalFieldOf("use_team_color", new ConstantBooleanProvider(true)).forGetter(ModifyGlowingOtherPower::getUseTeamColor))
		.and(Color.CODEC.optionalFieldOf("color", Argb.DEFAULT).forGetter(ModifyGlowingOtherPower::getColor))
		.apply(instance, ModifyGlowingOtherPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyGlowingOtherPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition,
		BooleanProvider.PACKET_CODEC, ModifyGlowingOtherPower::getUseTeamColor,
		Color.PACKET_CODEC, ModifyGlowingOtherPower::getColor,
		ModifyGlowingOtherPower::new
	);

	private final BooleanProvider useTeamColor;
	private final Color color;

	public ModifyGlowingOtherPower(Optional<Condition> activeCondition, BooleanProvider useTeamColor, Color color) {
		super(activeCondition);
		this.useTeamColor = useTeamColor;
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_GLOWING_OTHER_POWER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getUseTeamColor().validate(reporter.makeChild(".use_team_color"));
		getColor().validate(reporter.makeChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyGlowingOtherPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyGlowingOtherPower power) {
			super(holder, power);
		}

		public int getColor(Context context) {
			return this.getPower().getColor().getValue(context.makeChild(".color"));
		}

		public boolean shouldUseTeamColor(Context context) {
			return this.getPower().getUseTeamColor().next(context.makeChild(".use_team_color"));
		}

	}

	public static Context createContext(Entity actor, Entity target) {
		return PowerTypes.MODIFY_GLOWING_SELF_POWER.contextBuilder()
			.add(ContextParameters.ACTOR, actor)
			.add(ContextParameters.TARGET, target)
			.add(ContextParameters.THIS_ENTITY, actor)
			.add(ContextParameters.ENTITY_POS, actor.getPos())
			.build(target.getWorld());
	}

}
