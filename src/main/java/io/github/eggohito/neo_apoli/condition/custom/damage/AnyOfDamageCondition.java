package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfDamageCondition(List<DamageCondition> conditions) implements DamageCondition, AnyOfMetaCondition<DamageCondition> {

	public static final MapCodec<AnyOfDamageCondition> CODEC = MapCodecUtil.lazy(AnyOfDamageCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(DamageCondition.CODEC, AnyOfDamageCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfDamageCondition> PACKET_CODEC = PacketCodecUtil.lazy(AnyOfDamageCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(DamageCondition.PACKET_CODEC, AnyOfDamageCondition::new));

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.ANY_OF;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
