package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.AllOfBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, AllOfMetaCondition<EntityCondition, EntityConditionType<?>> {

	public static final MapCodec<AllOfEntityCondition> CODEC = NeoApoliMapCodecs.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(EntityCondition.CODEC, AllOfEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, AllOfEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ALL_OF;
	}

}
