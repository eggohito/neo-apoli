package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedCondition(Condition condition) implements InvertedMetaCondition<Condition> {

	public static final MapCodec<InvertedCondition> CODEC = MapCodecUtil.lazy(InvertedCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(Condition.CODEC, InvertedCondition::new));

	public static final PacketCodec<RegistryByteBuf, InvertedCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(Condition.PACKET_CODEC, InvertedCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.INVERTED;
	}

}
