package io.github.eggohito.neo_apoli.condition.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface ConditionType<C extends Condition<?>> {

	MapCodec<C> mapCodec();

	PacketCodec<RegistryByteBuf, C> packetCodec();

}
