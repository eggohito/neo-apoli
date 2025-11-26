package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.custom.ConstantCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Condition extends ContextAware, StringDisplayable {

	Codec<Condition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(ConditionType.CODEC.dispatch(Condition::getType, ConditionType::mapCodec), ConstantCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, Condition> STREAM_CODEC = ConditionType.STREAM_CODEC.dispatch(Condition::getType, ConditionType::packetCodec);

	@Override
	default String asDisplayString() {
		return "Condition with type \"%s\"".formatted(RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()));
	}

	ConditionType<?> getType();

	boolean test(Context context);

}
