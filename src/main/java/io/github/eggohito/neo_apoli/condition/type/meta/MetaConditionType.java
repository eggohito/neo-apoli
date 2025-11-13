package io.github.eggohito.neo_apoli.condition.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.MetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record MetaConditionType<C extends MetaCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

}
