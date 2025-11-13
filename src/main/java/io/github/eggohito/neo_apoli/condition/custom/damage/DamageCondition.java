package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface DamageCondition extends Condition {

	Codec<DamageCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(DamageConditionType.CODEC.dispatch(DamageCondition::getType, DamageConditionType::mapCodec), ConstantDamageCondition.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, DamageCondition> PACKET_CODEC = DamageConditionType.PACKET_CODEC.dispatch(DamageCondition::getType, DamageConditionType::packetCodec);

	@Override
	DamageConditionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return ContextTypes.DAMAGE.getRequired();
	}

	@Override
	default String asDisplayString() {
		return "Damage condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
