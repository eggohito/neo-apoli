package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, AnyOfMetaCondition<EntityCondition, EntityConditionType<?>> {

	public static final MapCodec<AnyOfEntityCondition> CODEC = NeoApoliCodecs.lazyMap("AnyOfEntityCondition", () -> AnyOfMetaCondition.codec(EntityCondition.CODEC, AnyOfEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy("AnyOfEntityCondition", () -> AnyOfMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, AnyOfEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ANY_OF;
	}

}
