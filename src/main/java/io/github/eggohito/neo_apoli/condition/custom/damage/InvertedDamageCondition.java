package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedDamageCondition(DamageCondition condition) implements DamageCondition, InvertedMetaCondition<DamageCondition> {

	public static final MapCodec<InvertedDamageCondition> CODEC = MapCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(DamageCondition.CODEC, InvertedDamageCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedDamageCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(DamageCondition.PACKET_CODEC, InvertedDamageCondition::new));

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
