package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyModelShakingPower extends Power {

	public static final MapCodec<ModifyModelShakingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance).apply(instance, ModifyModelShakingPower::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyModelShakingPower> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition, ModifyModelShakingPower::new);

	public ModifyModelShakingPower(Optional<Condition> activeCondition) {
		super(activeCondition);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_MODEL_SHAKING;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<ModifyModelShakingPower> {

		protected Instance(@NotNull ModifyModelShakingPower power) {
			super(power);
		}

		public boolean isActive(Entity holder) {
			return this.isActive(this.createHolderContext(holder));
		}

	}

}
