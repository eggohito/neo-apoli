package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedEntityCondition(EntityCondition condition) implements EntityCondition, InvertedMetaCondition<EntityCondition, EntityConditionType<?>> {

	public static final MapCodec<InvertedEntityCondition> CODEC = NeoApoliCodecs.lazyMap("InvertedEntityCondition", () -> InvertedMetaCondition.createCodec(EntityCondition.CODEC, InvertedEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy("InvertedEntityCondition", () -> InvertedMetaCondition.createPacketCodec(EntityCondition.PACKET_CODEC, InvertedEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.INVERTED;
	}

}
