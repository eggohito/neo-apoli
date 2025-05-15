package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, AllOfMetaCondition<EntityCondition, EntityConditionType<?>> {

	public static final MapCodec<AllOfEntityCondition> CODEC = NeoApoliCodecs.lazyMap("AllOfEntityCondition", () -> AllOfMetaCondition.codec(EntityCondition.CODEC, AllOfEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy("AllOfEntityCondition", () -> AllOfMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, AllOfEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ALL_OF;
	}

}
