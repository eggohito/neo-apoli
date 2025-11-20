package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfCondition(List<Condition> conditions) implements AnyOfMetaCondition<Condition> {

	public static final MapCodec<AnyOfCondition> CODEC = MapCodecUtil.lazy(AnyOfCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(Condition.CODEC, AnyOfCondition::new));

	public static final PacketCodec<RegistryByteBuf, AnyOfCondition> PACKET_CODEC = PacketCodecUtil.lazy(AnyOfCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(Condition.PACKET_CODEC, AnyOfCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.ANY_OF;
	}

}
