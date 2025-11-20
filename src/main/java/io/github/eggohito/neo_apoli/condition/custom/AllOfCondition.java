package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfCondition(List<Condition> conditions) implements AllOfMetaCondition<Condition> {

	public static final MapCodec<AllOfCondition> CODEC = MapCodecUtil.lazy(AllOfCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(Condition.CODEC, AllOfCondition::new));

	public static final PacketCodec<RegistryByteBuf, AllOfCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(Condition.PACKET_CODEC, AllOfCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.ALL_OF;
	}

}
