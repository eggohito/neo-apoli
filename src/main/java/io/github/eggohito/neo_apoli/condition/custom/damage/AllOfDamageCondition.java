package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfDamageCondition(List<DamageCondition> conditions) implements DamageCondition, AllOfMetaCondition<DamageCondition> {

	public static final MapCodec<AllOfDamageCondition> CODEC = MapCodecUtil.lazy(AllOfDamageCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(DamageCondition.CODEC, AllOfDamageCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfDamageCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfDamageCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(DamageCondition.PACKET_CODEC, AllOfDamageCondition::new));

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
