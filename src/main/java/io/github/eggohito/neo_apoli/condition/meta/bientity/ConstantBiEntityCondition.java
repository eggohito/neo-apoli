package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantBiEntityCondition(boolean value) implements BiEntityCondition, ConstantMetaCondition<BiEntityConditionType<?>> {

	public static final MapCodec<ConstantBiEntityCondition> CODEC = ConstantMetaCondition.codec(ConstantBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantBiEntityCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantBiEntityCondition::new).cast();

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.CONSTANT;
	}

}
