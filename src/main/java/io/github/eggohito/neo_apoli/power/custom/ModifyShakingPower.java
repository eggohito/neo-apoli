package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public class ModifyShakingPower extends Power {

	public static final MapCodec<ModifyShakingPower> CODEC = createSimpleConditionedCodec(ModifyShakingPower::new);
	public static final PacketCodec<RegistryByteBuf, ModifyShakingPower> PACKET_CODEC = createSimpleConditionedPacketCodec(ModifyShakingPower::new);

	public ModifyShakingPower(Properties properties, EntityCondition activeCondition) {
		super(properties, activeCondition);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_SHAKING;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends Power.Impl<ModifyShakingPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyShakingPower power) {
			super(holder, power);
		}

		public boolean isActive() {
			return this.isActive(this.genericContext());
		}

	}

}
