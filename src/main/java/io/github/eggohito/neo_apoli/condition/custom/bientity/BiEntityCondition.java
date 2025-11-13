package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BiEntityCondition extends Condition {

	Codec<BiEntityCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BiEntityConditionType.CODEC.dispatch(BiEntityCondition::getType, BiEntityConditionType::mapCodec), ConstantBiEntityCondition.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, BiEntityCondition> PACKET_CODEC = BiEntityConditionType.PACKET_CODEC.dispatch(BiEntityCondition::getType, BiEntityConditionType::packetCodec);

	@Override
	BiEntityConditionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return ContextTypes.BIENTITY.getAllowed();
	}

	@Override
	default String asDisplayString() {
		return "Bi-entity condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
