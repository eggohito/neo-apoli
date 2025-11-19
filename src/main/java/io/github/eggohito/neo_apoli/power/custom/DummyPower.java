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

import java.util.Optional;

public class DummyPower extends Power {

	public static final MapCodec<DummyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance).apply(instance, DummyPower::new));
	public static final PacketCodec<RegistryByteBuf, DummyPower> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition, DummyPower::new);

	public DummyPower(Optional<Condition> activeCondition) {
		super(activeCondition);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.DUMMY;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance<>(holder, this) {};
	}

}
