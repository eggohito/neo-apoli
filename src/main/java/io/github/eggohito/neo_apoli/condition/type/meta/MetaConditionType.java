package io.github.eggohito.neo_apoli.condition.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.MetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MetaConditionType<C extends MetaCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) implements ConditionType<C> {

}
