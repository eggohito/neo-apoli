package io.github.eggohito.neo_apoli.condition.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record EntityConditionType<C extends EntityCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

}
