package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Condition extends ContextUser {

	Codec<Condition> CODEC = ConditionType.CODEC.dispatch(Condition::getType, ConditionType::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, Condition> STREAM_CODEC = ConditionType.STREAM_CODEC.dispatch(Condition::getType, ConditionType::streamCodec);

	ConditionType<?> getType();

	boolean test(Context context);

}
