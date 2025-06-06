package io.github.eggohito.neo_apoli.condition.type.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ItemConditionType<C extends ItemCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

}
