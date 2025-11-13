package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfBiEntityCondition(List<BiEntityCondition> conditions) implements BiEntityCondition, AllOfMetaCondition<BiEntityCondition> {

	public static final MapCodec<AllOfBiEntityCondition> CODEC = MapCodecUtil.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(BiEntityCondition.CODEC, AllOfBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfBiEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, AllOfBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
