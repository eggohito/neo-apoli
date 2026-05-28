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

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class DummyPower extends Power {

	public static final MapCodec<DummyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance).apply(instance, DummyPower::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DummyPower> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition, DummyPower::new);

	public DummyPower(Optional<Condition> activeCondition) {
		super(activeCondition);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.DUMMY;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance<>(this) {};
	}

}
