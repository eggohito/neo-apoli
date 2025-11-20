package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyModelShakingPower extends Power {

	public static final MapCodec<ModifyModelShakingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance).apply(instance, ModifyModelShakingPower::new));
	public static final PacketCodec<RegistryByteBuf, ModifyModelShakingPower> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition, ModifyModelShakingPower::new);

	public ModifyModelShakingPower(Optional<Condition> activeCondition) {
		super(activeCondition);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_MODEL_SHAKING;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<ModifyModelShakingPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyModelShakingPower power) {
			super(holder, power);
		}

		public boolean isActive() {
			return this.isActive(this.createHolderContext());
		}

	}

}
