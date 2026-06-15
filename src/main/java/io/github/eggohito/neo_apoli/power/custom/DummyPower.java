package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record DummyPower(Optional<Condition> activeCondition) implements Power {

	public static final MapCodec<DummyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> Power.addActiveConditionField(instance).apply(instance, DummyPower::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DummyPower> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::activeCondition, DummyPower::new);

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.DUMMY;
	}

	@Override
	public Instance<?> createInstance() {
		return new Instance<>(this) {};
	}

}
